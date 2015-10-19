package seng302.group2.workspace.categories.subCategory.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.scenes.dialog.CreateBacklogDialog;
import seng302.group2.scenes.sceneswitch.switchStrategies.category.subCategory.project.BacklogCategorySwitchStrategy;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.categories.subCategory.SubCategory;
import seng302.group2.workspace.project.Project;

/**
 * THe category item for Backlogs (shown under each projects)
 * Created by cvs20 on 19/05/15.
 */
public class BacklogCategory extends SubCategory {

    /**
     * Basic constructor for a TreeView category
     *
     * @param project The parent project of this release category
     */
    public BacklogCategory(Project project) {
        super("Backlogs", project);
        setCategorySwitchStrategy(new BacklogCategorySwitchStrategy());
    }

    /**
     * Gets the Backlogs category's project
     *
     * @return the story category's project
     */
    public Project getProject() {
        return (Project) parent;
    }

    /**
     * Method for creating an XML element for the Team within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element backlogElements = ReportGenerator.doc.createElement("backlogs");
        for (Object item : getChildren()) {
            if (ReportGenerator.generatedItems.contains((SaharaItem) item)) {
                Element xmlElement = ((SaharaItem) item).generateXML();
                if (xmlElement != null) {
                    backlogElements.appendChild(xmlElement);
                }
                ReportGenerator.generatedItems.remove(item);
            }
        }
        return backlogElements;
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
        return ((Project) parent).getBacklogs();
    }

    /**
     * Shows the Backlog Creation dialog.
     */
    @Override
    public void showCreationDialog() {
        javafx.scene.control.Dialog creationDialog = new CreateBacklogDialog(getProject());
        creationDialog.show();
    }
}
