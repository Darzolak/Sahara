package seng302.group2.workspace.categories;

import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.workspace.SaharaItem;

/**
 * Category item in the list display, uses as a header for all the elements of one type, ie
 * "People" for all persons within the workspace.
 *
 * @author Jordane
 */
public abstract class Category extends SaharaItem {
    private String name;


    /**
     * Basic constructor for a category
     *
     * @param name The name of the category
     */
    public Category(String name) {
        super(name);
        this.name = name;
    }

    /**
     * Method for creating an XML element for the Team within report generation
     * @return element for XML generation
     */
    public Element generateXML() {
        return null;
    }


    /**
     * An overridden version for the String representation of a Person
     *
     * @return The short name of the Person
     */
    @Override
    public String toString() {
        return this.name;
    }


    /**
     * Gets the children of the category
     *
     * @return the children of the category
     */
    @Override
    public abstract ObservableList<SaharaItem> getChildren();


    /**
     * Shows the creation dialog for the types of items of the category
     */
    public abstract void showCreationDialog();


    /**
     * Overrides that a category is equal if it has the same children
     *
     * @param obj the object to compare
     * @return if the objects are equal/equivalent
     */
    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(this.getClass())
                && ((Category) obj).getChildren() == this.getChildren()
                && ((Category) obj).name.equals(this.name);
    }


    /**
     * Overrides that a category is equal if it has the same children
     *
     * @param obj the object to compare
     * @return if the objects are equal/equivalent
     */
    @Override
    public boolean equivalentTo(Object obj) {
        return obj.getClass().equals(this.getClass())
                && ((Category) obj).getChildren() == this.getChildren()
                && ((Category) obj).name.equals(this.name);
    }
}
