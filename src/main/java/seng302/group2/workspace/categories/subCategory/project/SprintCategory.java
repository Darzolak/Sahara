package seng302.group2.workspace.categories.subCategory.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.scenes.dialog.CreateSprintDialog;
import seng302.group2.scenes.sceneswitch.switchStrategies.category.subCategory.project.SprintCategorySwitchStrategy;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.categories.subCategory.SubCategory;
import seng302.group2.workspace.project.Project;

/**
 *  Category item in the list display, uses as a header for all the elements of one type, ie
 * "People" for all persons within the workspace.
 *
 * Created by drm127 on 29/07/15.
 */
public class SprintCategory extends SubCategory {

    /**
     * Basic constructor for a tree view category
     *
     * @param project the parent project of this sprint category
     */
    public SprintCategory(Project project) {
        super("Sprints", project);
        setCategorySwitchStrategy(new SprintCategorySwitchStrategy());
    }

    /**
     * Gets the sprint category's project
     *
     * @return the sprint category's project
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
        return ((Project) parent).getSprints();
    }

    /**
     * Method for creating an XML element for the Sprint within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element releaseElements = ReportGenerator.doc.createElement("sprints");
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
     * Shows the Sprint Creation dialog.
     */
    @Override
    public void showCreationDialog() {
        javafx.scene.control.Dialog creationDialog = new CreateSprintDialog(getProject());
        creationDialog.show();
    }
}
