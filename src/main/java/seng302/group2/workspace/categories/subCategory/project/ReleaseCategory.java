package seng302.group2.workspace.categories.subCategory.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.scenes.dialog.CreateReleaseDialog;
import seng302.group2.scenes.sceneswitch.switchStrategies.category.subCategory.project.ReleaseCategorySwitchStrategy;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.categories.subCategory.SubCategory;
import seng302.group2.workspace.project.Project;

/**
 * Category item in the list display, uses as a header for all the elements of one type, ie
 * "People" for all persons within the workspace.
 *
 * @author Jordane
 */
public class ReleaseCategory extends SubCategory {

    /**
     * Basic constructor for a TreeView category
     *
     * @param project The parent project of this release category
     */
    public ReleaseCategory(Project project) {
        super("Releases", project);
        setCategorySwitchStrategy(new ReleaseCategorySwitchStrategy());
    }

    /**
     * Gets the release category's project
     *
     * @return the release category's project
     */
    public Project getProject() {
        return (Project) parent;
    }

    /**
     * Gets the children of the category
     *
     * @return the children of the category
     */
    @Override
    public ObservableList getChildren() {
        if (parent == null) {
            return FXCollections.observableArrayList();
        }
        return ((Project) parent).getReleases();
    }

    /**
     * Method for creating an XML element for the Release within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element releaseElements = ReportGenerator.doc.createElement("releases");
        for (Object item : getChildren()) {
            if (ReportGenerator.generatedItems.contains((SaharaItem) item)) {
                Element xmlElement = ((SaharaItem) item).generateXML();
                if (xmlElement != null) {
                    releaseElements.appendChild(xmlElement);
                }
                ReportGenerator.generatedItems.remove(item);
            }
        }
        return releaseElements;
    }

    /**
     * Shows the Release Creation dialog.
     */
    @Override
    public void showCreationDialog() {
        javafx.scene.control.Dialog creationDialog = new CreateReleaseDialog(getProject());
        creationDialog.show();
    }
}
