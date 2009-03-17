package configurationslicing;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.ManagementLink;
import hudson.model.Descriptor.FormException;
import hudson.tasks.Builder;

@Extension
public class ConfigurationSlicing extends ManagementLink {

    @Override
    public String getIconFileName() {
        return "orange-square.gif";
    }

    @Override
    public String getUrlName() {
        return "slicing";
    }

    public String getDisplayName() {
        return "Configuration Slicing";
    }

    public ExtensionList<Slicer> getAxes() {
        return Hudson.getInstance().getExtensionList(Slicer.class);
    }
    
    public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp) {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        for(Slicer s : getAxes()) {
            if(s.getUrl().equals(token)) {
                return new SliceExecutor(s);
            }
        }
        return null;
    }

    public class SliceExecutor<T extends Slice,I> {
        Slicer<T,I> slicer;
        List<I> worklist;
        List<I> changed;
        T slice;
        public SliceExecutor(Slicer<T, I> s) {
            this.slicer = s;
            execute();
        }
        
        private void execute() {
            T accumulator = slicer.getInitialAccumulator();
            worklist = slicer.getWorkDomain();
            for(I item : worklist) {
                accumulator = slicer.accumulate(accumulator, item);
            }
            slice= accumulator;
        }
        
        private List<I> transform(T newslice) {
            List<I> ret = new ArrayList<I>();
            worklist = slicer.getWorkDomain();
            for(I item : worklist) {
                if(slicer.transform(newslice, item))
                    ret.add(item);
            }
            return ret;
        }
        
        public ConfigurationSlicing getParent() {
            return ConfigurationSlicing.this;
        }
        
        public T getSlice() {
            return slice;
        }
             
        public List<I> getChanged() {
            return changed;
        }
        
        public List<I> getWorklist() {
            return worklist;
        }
        
        public void doSliceconfigSubmit( StaplerRequest req, StaplerResponse rsp ) throws IOException, ServletException {
            try {
                T newslice = (T)slice.newInstance(req, req.getSubmittedForm());
                transform(newslice);
                this.slice = newslice;
                rsp.forward(this, "changesummary", req);
            } catch (FormException e) {
                e.printStackTrace();
            }
        }
    }
}
