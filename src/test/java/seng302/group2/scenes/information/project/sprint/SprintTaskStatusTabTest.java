package seng302.group2.scenes.information.project.sprint;

import org.junit.BeforeClass;
import seng302.group2.scenes.JavaFxTestApp;


public class SprintTaskStatusTabTest {

    @BeforeClass
    public static void before() {
        JavaFxTestApp.initJFX();
    }

    /*@Test
    public void testCollectTasksUnassigned() throws Exception {
        Project project = new Project();
        Team team = new Team();
        Release release = new Release();
        Story story_ = new Story();

        Sprint testSprint = new Sprint("Goal", "LongName", "Description", LocalDate.now(), LocalDate.now().plusDays(5),
                project, team, release);
        Task testTask = new Task("Test Task", "test description", story_, null, 0);

        story_.add(testTask);
        testSprint.add(story_);

        List<Task> tasks = new ArrayList<>();
        tasks.addAll(testSprint.getAllTasks());

        SprintTaskStatusTab taskStatusTab = new SprintTaskStatusTab(testSprint);
        taskStatusTab.group.selectToggle(taskStatusTab.statusToggle.getRadioButton());
        taskStatusTab.filterBox.getComboBox().setValue("Unassigned");

        List<Task> statusFilteredTasks = taskStatusTab.collectTasks();

        Assert.assertTrue(statusFilteredTasks.contains(testTask));

        taskStatusTab.group.selectToggle(taskStatusTab.storyToggle.getRadioButton());
        taskStatusTab.filterBox.getComboBox().setValue("Unassigned");

        List<Task> storyFilteredTasks = taskStatusTab.collectTasks();

        Assert.assertTrue(storyFilteredTasks.contains(testTask));
    }

    @Test
    public void testCollectTasksUncompleted() throws Exception {


        Project project = new Project();
        Team team = new Team();
        Release release = new Release();
        Story story_ = new Story();

        Sprint testSprint = new Sprint("Goal", "LongName", "Description", LocalDate.now(), LocalDate.now().plusDays(5),
                project, team, release);
        Task testTask = new Task("Test Task", "test description", story_, null, 0);

        story_.add(testTask);
        testSprint.add(story_);

        List<Task> tasks = new ArrayList<>();
        tasks.addAll(testSprint.getAllTasks());

        SprintTaskStatusTab taskStatusTab = new SprintTaskStatusTab(testSprint);
        taskStatusTab.group.selectToggle(taskStatusTab.statusToggle.getRadioButton());
        taskStatusTab.filterBox.getComboBox().setValue("Uncompleted");

        List<Task> statusFilteredTasks = taskStatusTab.collectTasks();

        Assert.assertTrue(statusFilteredTasks.contains(testTask));

        taskStatusTab.group.selectToggle(taskStatusTab.storyToggle.getRadioButton());
        taskStatusTab.filterBox.getComboBox().setValue("Uncompleted");

        List<Task> storyFilteredTasks = taskStatusTab.collectTasks();

        Assert.assertTrue(storyFilteredTasks.contains(testTask));
    }

    @Test
    public void testCollectTasksAll() throws Exception {


        Project project = new Project();
        Team team = new Team();
        Release release = new Release();
        Story story_ = new Story();

        Sprint testSprint = new Sprint("Goal", "LongName", "Description", LocalDate.now(), LocalDate.now().plusDays(5),
                project, team, release);
        Task testTask = new Task("Test Task", "test description", story_, null, 0);

        story_.add(testTask);
        testSprint.add(story_);

        List<Task> tasks = new ArrayList<>();
        tasks.addAll(testSprint.getAllTasks());

        SprintTaskStatusTab taskStatusTab = new SprintTaskStatusTab(testSprint);
        taskStatusTab.group.selectToggle(taskStatusTab.statusToggle.getRadioButton());
        taskStatusTab.filterBox.getComboBox().setValue("All");

        List<Task> statusFilteredTasks = taskStatusTab.collectTasks();

        Assert.assertTrue(statusFilteredTasks.contains(testTask));

        taskStatusTab.group.selectToggle(taskStatusTab.storyToggle.getRadioButton());
        taskStatusTab.filterBox.getComboBox().setValue("All");

        List<Task> storyFilteredTasks = taskStatusTab.collectTasks();

        Assert.assertTrue(storyFilteredTasks.contains(testTask));
    }*/
}