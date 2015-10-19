package seng302.group2.workspace.categories;

import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.scenes.dialog.CreateProjectDialog;
import seng302.group2.scenes.sceneswitch.switchStrategies.category.ProjectCategorySwitchStrategy;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.SaharaItem;

import java.util.HashSet;
import java.util.Set;

/**
 * A category that has the current workspace's projects as children
 * Created by Jordane on 7/06/2015.
 */
public class ProjectCategory extends Category {
    
    /**
     * Constructor for the ProjectCategory class.
     */
    public ProjectCategory() {
        super("Projects");
        setCategorySwitchStrategy(new ProjectCategorySwitchStrategy());
    }

    /**
     * Method for creating an XML element for the Team within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element projectElements = ReportGenerator.doc.createElement("projects");
        for (Object item : getChildren()) {
            if (ReportGenerator.generatedItems.contains((SaharaItem) item)) {
                Element xmlElement = ((SaharaItem) item).generateXML();
                if (xmlElement != null) {
                    projectElements.appendChild(xmlElement);
                }
                ReportGenerator.generatedItems.remove(item);
            }
        }
        return projectElements;
    }

    /**
     * Returns the items held by the ProjectCategory, blank as the project category has no child items.
     * @return a blank hash set
     */
    @Override
    public Set<SaharaItem> getItemsSet() {
        return new HashSet<>();
    }

    /**
     * Returns a list of all the projects in the workspace.
     * @return An ObservableList of the Projects in the workspace.
     */
    @Override
    public ObservableList getChildren() {
        return Global.currentWorkspace.getProjects();
    }

    /**
     * Shows the project creation dialog.
     */
    @Override
    public void showCreationDialog() {
        javafx.scene.control.Dialog creationDialog = new CreateProjectDialog();
        creationDialog.show();
    }
}
