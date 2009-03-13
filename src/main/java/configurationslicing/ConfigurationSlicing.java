package configurationslicing;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.ManagementLink;
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
        
        public ConfigurationSlicing getParent() {
            return ConfigurationSlicing.this;
        }
        
        public T getSlice() {
            return slice;
        }
                
        public Object getDescriptors() {
            return Hudson.getInstance().getDescriptorList(Slice.class);
        }
        public int getDescriptorIndex() {
            return Hudson.getInstance().getDescriptorList(Builder.class).size();
        }
        
        public void doSliceconfigSubmit( StaplerRequest req, StaplerResponse rsp ) throws IOException, ServletException {
            
        }
    }
}
