package seng302.group2.workspace.categories;

import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.scenes.dialog.CreateSkillDialog;
import seng302.group2.scenes.sceneswitch.switchStrategies.category.SkillsCategoryCategorySwitchStrategy;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.person.Person;

import java.util.HashSet;
import java.util.Set;

/**
 * A category that has the current workspace's roles as children
 * Created by Jordane on 7/06/2015.
 */
public class SkillsCategory extends Category {
    
    /**
     * Constructor for the SkillsCategory class.
     */
    public SkillsCategory() {
        super("Skills");
        setCategorySwitchStrategy(new SkillsCategoryCategorySwitchStrategy());
    }

    /**
     * Method for creating an XML element for the Team within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element skillElements = ReportGenerator.doc.createElement("unassigned-skills");
        for (Object item : getChildren()) {
            if (ReportGenerator.generatedItems.contains((SaharaItem) item)) {
                boolean assigned = false;
                for (Person person : Global.currentWorkspace.getPeople()) {
                    if (person.getSkills().contains(((SaharaItem) item))) {
                        assigned = true;
                        break;
                    }
                }
                if (!assigned) {
                    Element xmlElement = ((SaharaItem) item).generateXML();
                    if (xmlElement != null) {
                        skillElements.appendChild(xmlElement);
                    }
                }
                ReportGenerator.generatedItems.remove(item);
            }
        }
        return skillElements;
    }

    /**
     * Returns the items held by the SkillsCategory, blank as the skills category has no child items.
     * @return a blank hash set
     */
    @Override
    public Set<SaharaItem> getItemsSet() {
        return new HashSet<>();
    }

    /**
     * Returns a list of all the skills in the workspace.
     * @return An ObservableList of the Skills in the workspace.
     */
    @Override
    public ObservableList getChildren() {
        return Global.currentWorkspace.getSkills();
    }

    /**
     * Shows the Skill creation dialog.
     */
    @Override
    public void showCreationDialog() {
        javafx.scene.control.Dialog creationDialog = new CreateSkillDialog();
        creationDialog.show();
    }
}
