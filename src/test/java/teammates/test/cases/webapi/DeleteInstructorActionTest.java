package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.InstructorsLogic;
import teammates.ui.webapi.action.DeleteInstructorAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link DeleteInstructorAction}.
 */
public class DeleteInstructorActionTest extends BaseActionTest<DeleteInstructorAction> {

    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    protected void testExecute() {
        //see test cases below
    }

    @Test
    protected void testExecute_typicalCaseByGoogleId_shouldPass() {
        ______TS("Typical case: admin deletes an instructor by google id");

        loginAsAdmin();

        InstructorAttributes instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");
        String instructorId = instructor1OfCourse2.googleId;

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, instructor1OfCourse2.courseId,
        };

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());

        assertFalse(instructorsLogic.isEmailOfInstructorOfCourse(instructor1OfCourse2.email, instructor1OfCourse2.courseId));

        ______TS("Typical case: instructor deletes another instructor by google id");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructor2OfCourse1.googleId,
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        assertTrue(logic.getInstructorsForCourse(instructor1OfCourse1.courseId).size() > 1);

        deleteInstructorAction = getAction(submissionParams);
        response = getJsonResult(deleteInstructorAction);

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());

        assertFalse(instructorsLogic.isEmailOfInstructorOfCourse(instructor2OfCourse1.email, instructor1OfCourse1.courseId));
        assertTrue(instructorsLogic.isEmailOfInstructorOfCourse(instructor1OfCourse1.email, instructor1OfCourse1.courseId));

    }

    @Test
    public void testExecute_deleteInstructorByEmail_shouldSuccess() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor2OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        assertTrue(logic.getInstructorsForCourse(instructor1OfCourse1.courseId).size() > 1);

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());

        assertFalse(instructorsLogic.isEmailOfInstructorOfCourse(instructor2OfCourse1.email, instructor1OfCourse1.courseId));
        assertTrue(instructorsLogic.isEmailOfInstructorOfCourse(instructor1OfCourse1.email, instructor1OfCourse1.courseId));
    }

    @Test
    protected void testExecute_adminDeletesLastInstructorByGoogleId_shouldPass() {
        loginAsAdmin();

        InstructorAttributes instructor4 = typicalBundle.instructors.get("instructor4");
        String instructorId = instructor4.googleId;

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, instructor4.courseId,
        };

        assertEquals(logic.getInstructorsForCourse(instructor4.courseId).size(), 1);

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());

        assertFalse(instructorsLogic.isEmailOfInstructorOfCourse(instructor4.email, instructor4.courseId));
    }

    @Test
    protected void testExecute_instructorDeleteOwnRoleByGoogleId_shouldPass() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor2OfCourse1.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructor2OfCourse1.googleId,
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        assertTrue(logic.getInstructorsForCourse(instructor1OfCourse1.courseId).size() > 1);

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());

        InstructorsLogic instructorsLogic = InstructorsLogic.inst();

        assertFalse(instructorsLogic.isEmailOfInstructorOfCourse(instructor2OfCourse1.email, instructor1OfCourse1.courseId));
        assertTrue(instructorsLogic.isEmailOfInstructorOfCourse(instructor1OfCourse1.email, instructor1OfCourse1.courseId));
    }

    @Test
    protected void testExecute_deleteLastInstructorByGoogleId_shouldFail() {
        InstructorAttributes instructorToDelete = typicalBundle.instructors.get("instructor4");
        String courseId = instructorToDelete.courseId;

        loginAsInstructor(instructorToDelete.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorToDelete.googleId,
        };

        assertEquals(logic.getInstructorsForCourse(courseId).size(), 1);

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCode());

        MessageOutput messageOutput = (MessageOutput) response.getOutput();
        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", messageOutput.getMessage());

        assertTrue(instructorsLogic.isEmailOfInstructorOfCourse(instructorToDelete.email, courseId));
        assertTrue(instructorsLogic.isGoogleIdOfInstructorOfCourse(instructorToDelete.googleId, courseId));
    }

    @Test
    protected void testExecute_deleteLastInstructorInMasqueradeByGoogleId_shouldFail() {
        InstructorAttributes instructorToDelete = typicalBundle.instructors.get("instructor4");
        String courseId = instructorToDelete.courseId;

        loginAsAdmin();

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorToDelete.googleId,
        };

        assertEquals(logic.getInstructorsForCourse(courseId).size(), 1);

        DeleteInstructorAction deleteInstructorAction =
                getAction(addUserIdToParams(instructorToDelete.googleId, submissionParams));
        JsonResult response = getJsonResult(deleteInstructorAction);

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCode());

        MessageOutput messageOutput = (MessageOutput) response.getOutput();
        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", messageOutput.getMessage());

        assertTrue(instructorsLogic.isEmailOfInstructorOfCourse(instructorToDelete.email, courseId));
        assertTrue(instructorsLogic.isGoogleIdOfInstructorOfCourse(instructorToDelete.googleId, courseId));
    }

    @Test
    protected void testExecute_deleteInstructorInMasqueradeByGoogleId_shouldPass() {
        InstructorAttributes instructorToDelete = typicalBundle.instructors.get("instructorNotDisplayedToStudent2");
        String courseId = instructorToDelete.courseId;

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorToDelete.googleId,
        };

        loginAsAdmin();

        assertTrue(logic.getInstructorsForCourse(courseId).size() > 1);

        DeleteInstructorAction deleteInstructorAction =
                getAction(addUserIdToParams(instructorToDelete.googleId, submissionParams));
        JsonResult response = getJsonResult(deleteInstructorAction);

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        MessageOutput messageOutput = (MessageOutput) response.getOutput();

        assertEquals("Instructor is successfully deleted.", messageOutput.getMessage());
        assertFalse(instructorsLogic.isEmailOfInstructorOfCourse(instructorToDelete.email, courseId));
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;

        String[] onlyInstructorParameter = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
        };

        String[] onlyCourseParameter = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        loginAsAdmin();

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(onlyInstructorParameter);
        verifyHttpParameterFailure(onlyCourseParameter);

        loginAsInstructor(instructorId);

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(onlyInstructorParameter);
        verifyHttpParameterFailure(onlyCourseParameter);
    }

    @Test
    protected void testExecute_noSuchInstructor_shouldFail() {
        loginAsAdmin();

        attemptToDeleteFakeInstructorByGoogleId();
        attemptToDeleteFakeInstructorByEmail();

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        attemptToDeleteFakeInstructorByGoogleId();
        attemptToDeleteFakeInstructorByEmail();
    }

    private void attemptToDeleteFakeInstructorByGoogleId() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, "fake-googleId",
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        assertNull(logic.getInstructorForGoogleId(instructor1OfCourse1.courseId, "fake-googleId"));

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());
    }

    private void attemptToDeleteFakeInstructorByEmail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "fake-instructor@fake-email",
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        assertNull(logic.getInstructorForEmail(instructor1OfCourse1.courseId, "fake-instructor@fake-email"));

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());
    }

    @Test
    protected void testExecute_adminDeletesInstructorInFakeCourse_shouldFail() {
        loginAsAdmin();

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, "fake-course",
        };

        assertNull(logic.getCourse("fake-course"));

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.email,
        };

        verifyInaccessibleWithoutModifyInstructorPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyAccessibleForAdmin(submissionParams);
    }

}
