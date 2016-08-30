package excel2er.services.finder;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IERModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class ERModelFinder {

    public IERModel find() throws ProjectNotFoundException, ClassNotFoundException {
        ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
        INamedElement[] result = projectAccessor.findElements(IERModel.class);
        if (result != null && result.length > 0) {
            return (IERModel) result[0];
        }
        return null;
    }

}
