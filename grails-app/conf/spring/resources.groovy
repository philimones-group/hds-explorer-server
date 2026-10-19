import dev.langchain4j.model.openai.OpenAiChatModel
import org.philimone.hds.explorer.server.model.audit.UserAuditListener
import org.philimone.hds.explorer.server.model.authentication.UserPasswordEncoderListener
// Place your Spring DSL code here
beans = {
    userPasswordEncoderListener(UserPasswordEncoderListener)
    userAuditListener(UserAuditListener)

    chatLanguageModel(OpenAiChatModel) {
        apiKey = System.getenv("OPENAI_API_KEY") ?: "demo"
        modelName = "gpt-4o"
        temperature = 0.0
        responseFormat = "json_object"
    }
}
