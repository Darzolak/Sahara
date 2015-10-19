package seng302.group2.workspace.categories;

import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.scenes.sceneswitch.switchStrategies.category.RoleCategorySwitchStrategy;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.SaharaItem;

import java.util.HashSet;
import java.util.Set;

/**
 * A category that has the current workspace's roles as children
 * Created by Jordane on 7/06/2015.
 */
public class RolesCategory extends Category {
    
    /**
     * Constructor for the RolesCategory class.
     */
    public RolesCategory() {
        super("Roles");
        setCategorySwitchStrategy(new RoleCategorySwitchStrategy());
    }

    /**
     * Returns the items held by the RolesCategory, blank as the roles category has no child items.
     * @return a blank hash set
     */
    @Override
    public Set<SaharaItem> getItemsSet() {
        return new HashSet<>();
    }

    /**
     * Returns a list of all the roles in the workspace.
     * @return An ObservableList of the Roles in the workspace.
     */
    @Override
    public ObservableList getChildren() {
        return Global.currentWorkspace.getRoles();
    }

    /**
     * Method for creating an XML element for the Role within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element roleElements = ReportGenerator.doc.createElement("roles");
        for (Object item : getChildren()) {
            if (ReportGenerator.generatedItems.contains((SaharaItem) item)) {
                Element xmlElement = ((SaharaItem) item).generateXML();
                if (xmlElement != null) {
                    roleElements.appendChild(xmlElement);
                }
                ReportGenerator.generatedItems.remove(item);
            }
        }
        return roleElements;
    }

    /**
     * Show the role creation dialog, if and when this is implemented.
     */
    @Override
    public void showCreationDialog() {
    }
}
