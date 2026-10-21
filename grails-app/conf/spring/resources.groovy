import org.philimone.hds.explorer.server.model.audit.UserAuditListener
import org.philimone.hds.explorer.server.model.authentication.UserPasswordEncoderListener
// Place your Spring DSL code here
beans = {
    userPasswordEncoderListener(UserPasswordEncoderListener)
    userAuditListener(UserAuditListener)
}
