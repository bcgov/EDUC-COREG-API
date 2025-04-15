package ca.bc.gov.educ.api.coreg.constants.v1;

public interface PermissionsConstants {
    String _PREFIX = "hasAuthority('";
    String _SUFFIX = "')";

    String READ_COREG_COURSE_DATA = _PREFIX + "SCOPE_COREG_READ_COURSE" + _SUFFIX;

}
