package configurationslicing;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Hudson;
import hudson.model.ManagementLink;
import hudson.model.Descriptor.FormException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

@Extension
public class ConfigurationSlicing extends ManagementLink {

    @Override
    public String getDescription() {
        return "Configure a single aspect across a group of items, in contrast to the traditional configuration of all aspects of a single item";
    }

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

    @SuppressWarnings("unchecked")
	public List<Slicer> getAxes() {
    	ExtensionList<Slicer> elist = Hudson.getInstance().getExtensionList(Slicer.class);
    	List<Slicer> list = new ArrayList<Slicer>();
    	for (Slicer slicer: elist) {
    		if (slicer.isLoaded()) {
    			list.add(slicer);
    		}
    	}
    	Collections.sort(list);
    	return list;
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
                // since we're not actually accumulating changes, let's not forward here anymore
//                rsp.forward(this, "changesummary", req);
                rsp.sendRedirect2("..");
            } catch (FormException e) {
                e.printStackTrace();
            }
        }
    }
}
