package configurationslicing;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Hudson;
import hudson.model.ManagementLink;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.model.ViewGroup;
import hudson.model.Descriptor.FormException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
        return "orange-square.png";
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
    
    public Collection<String> getViews() {
    	Collection<View> views = Hudson.getInstance().getViews();
    	List<String> names = new ArrayList<String>();
    	
    	addViews(null, views, names);
    	
    	Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
    	return names;
    }
    
    private void addViews(String baseName, Collection<View> views, List<String> names) {
    	for (View view: views) {
	    	String name = view.getDisplayName();
	    	if (baseName != null) {
	    		name = baseName + "/" + name;
	    	}
			if (view instanceof ViewGroup) {
				ViewGroup group = (ViewGroup) view;
				Collection<View> subviews = group.getViews();
				addViews(name, subviews, names);
			} else {
				names.add(name);
			}
    	}
    }

    public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp) {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        
        for(Slicer s : getAxes()) {
            if(s.getUrl().equals(token)) {
            	return new SliceExecutor(s, null);
            }
        }
        return null;
    }

    public class SliceExecutor<T extends Slice,I> {
        Slicer<T,I> slicer;
        List<I> worklist;
        List<I> changed;
        T slice;
        public SliceExecutor(Slicer<T, I> s, View view) {
            this.slicer = s;
            execute(view);
        }
        
        private void execute(View view) {
            T accumulator = slicer.getInitialAccumulator();
            worklist = slicer.getWorkDomain();
            Collection<TopLevelItem> items = null;
            if (view != null) {
            	items = view.getItems();
            }
            for(I item : worklist) {
            	if (items == null || items.contains(item)) {
            		accumulator = slicer.accumulate(accumulator, item);
            	}
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
        
		public Slicer<T, I> getSlicer() {
			return slicer;
		}
        
        public List<I> getChanged() {
            return changed;
        }
        
        public List<I> getWorklist() {
            return worklist;
        }
        
        public void doSliceconfigSubmit( StaplerRequest req, StaplerResponse rsp ) throws IOException, ServletException {
            String pathInfo = req.getPathInfo();
            try {
                T newslice = (T)slice.newInstance(req, req.getSubmittedForm());
                transform(newslice);
                this.slice = newslice;
                // since we're not actually accumulating changes, let's not forward here anymore
//                rsp.forward(this, "changesummary", req);
                String redirect = getRedirectPath(pathInfo);
                rsp.sendRedirect2(redirect);
            } catch (FormException e) {
                e.printStackTrace();
            }
        }
        private String getRedirectPath(String pathInfo) {
        	String urlPart = "slicing/" + slicer.getUrl() + "/view/"; 
        	if (pathInfo.contains(urlPart)) {
        		return "../..";
        	} else {
        		return "..";
        	}
        }
        public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp) {
            Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
            String viewName = req.getParameter("view");
            View view = null;
            if (viewName != null) {
            	view = getView(viewName);
            }
            return new SliceExecutor(slicer, view);
        }
        private View getView(String token) {
        	String[] split = token.split("/");
        	View view = null;
        	for (String name: split) {
        		if (view instanceof ViewGroup) {
        			view = ((ViewGroup) view).getView(name);
        		} else {
        			view = Hudson.getInstance().getView(name);
        		}
        	}
        	return view;
        }

    }
}
