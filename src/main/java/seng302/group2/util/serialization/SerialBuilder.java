package seng302.group2.util.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.allocation.Allocation;
import seng302.group2.workspace.categories.*;
import seng302.group2.workspace.categories.subCategory.SubCategory;
import seng302.group2.workspace.categories.subCategory.project.SprintCategory;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.backlog.Backlog;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.acceptanceCriteria.AcceptanceCriteria;
import seng302.group2.workspace.project.story.tasks.Log;
import seng302.group2.workspace.project.story.tasks.Task;
import seng302.group2.workspace.roadMap.RoadMap;
import seng302.group2.workspace.role.Role;
import seng302.group2.workspace.skills.Skill;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.team.Team;
import seng302.group2.workspace.workspace.Workspace;

import java.time.LocalDate;

/**
 * Created by Jordane on 12/04/2015.
 */
public class SerialBuilder {
    public static Gson getBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        new GraphAdapterBuilder()
                // Model classes
                .addType(SaharaItem.class)
                .addType(ObservableList.class)
                .addType(Workspace.class)
                .addType(Person.class)
                .addType(Skill.class)
                .addType(Team.class)
                .addType(Project.class)
                .addType(Release.class)
                .addType(Role.class)
                .addType(Allocation.class)
                .addType(Story.class)
                .addType(Task.class)
                .addType(LocalDate.class)
                .addType(Backlog.class)
                .addType(AcceptanceCriteria.class)
                .addType(Sprint.class)
                .addType(Task.class)
                .addType(Log.class)
                .addType(Tag.class)

                .addType(Color.class)
                .addType(Paint.class)

                .addType(RoadMap.class)


                        // TODO REMINDER: Add any new classes. Do not delete this todo.

                        // Category classes
                .addType(Category.class)
                .addType(SubCategory.class)
                .addType(PeopleCategory.class)
                .addType(ProjectCategory.class)
                .addType(RolesCategory.class)
                .addType(SkillsCategory.class)
                .addType(TeamsCategory.class)
                .addType(RoadMapCategory.class)
                .addType(SprintCategory.class)

                .registerOn(gsonBuilder);

        return gsonBuilder.create();
    }
}
