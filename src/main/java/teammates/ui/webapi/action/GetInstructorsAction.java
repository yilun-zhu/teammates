package teammates.ui.webapi.action;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.InstructorsData;

/**
 * Get a list of instructors of a course.
 */
public class GetInstructorsAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseAttributes course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("course not found"));
        }

        String intentStr = getRequestParamValue(Const.ParamsNames.INTENT);
        if (intentStr == null) {
            // get partial details of instructors with information hiding
            // student should belong to the course
            StudentAttributes student = logic.getStudentForGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(student, course);
        } else if (intentStr.equals(Intent.FULL_DETAIL.toString())) {
            // get all instructors of a course without information hiding
            // this need instructor privileges
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(instructor, course);
        } else {
            throw new InvalidHttpParameterException("unknown intent");
        }

    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        List<InstructorAttributes> instructorsOfCourse = logic.getInstructorsForCourse(courseId);

        InstructorsData data;

        String intentStr = getRequestParamValue(Const.ParamsNames.INTENT);
        if (intentStr == null) {
            instructorsOfCourse =
                    instructorsOfCourse.stream()
                            .filter(InstructorAttributes::isDisplayedToStudents)
                            .collect(Collectors.toList());
            data = new InstructorsData(instructorsOfCourse);

            // hide information
            data.getInstructors().forEach(i -> {
                i.setGoogleId(null);
                i.setJoinState(null);
                i.setIsDisplayedToStudents(null);
                i.setRole(null);
            });
        } else if (intentStr.equals(Intent.FULL_DETAIL.toString())) {
            // get all instructors of a course without information hiding
            data = new InstructorsData(instructorsOfCourse);
        } else {
            throw new InvalidHttpParameterException("unknown intent");
        }

        return new JsonResult(data);
    }

}
