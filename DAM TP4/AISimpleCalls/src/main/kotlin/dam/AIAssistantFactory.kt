package dam

import java.util.Properties

/**
 * AIAssistantFactory creates the appropriate AIAssistant implementation
 * based on configuration settings in the provided Properties object.
 */
class AIAssistantFactory {
    companion object {
        /**
         * Creates and returns an AIAssistant based on configuration
         *
         * @param properties Configuration properties containing API keys and settings
         * @return An implementation of AIAssistant (either OpenAI or Gemini)
         */




        //le do config.properties e instancia a classe conrrespodente, neste caso a nvidia e passa os valores por properties
        //NVIDIA em getProperty("AI_LLM", "NVIDIA") é o valor por defeito, se AI_LLM não existir no ficheiro, usa NVIDIA
        fun createAssistant(properties: Properties): AIAssistant {
            // Determine which assistant to create based on configuration
            return when (properties.getProperty("AI_LLM", "NVIDIA")) { //alterei de openai para nvidia
                "OPENAI" -> AIAssistantOpenAI(properties)
                "GEMINI" -> AIAssistantGemini(properties)
                "OPENAI-CLASSES" -> AIAssistantOpenAIClasses(properties)
                "GEMINI-CLASSES" -> AIAssistantGeminiClasses(properties)
                "NVIDIA-CLASSES" -> AIAssistantOpenAIClasses(properties) //adicionei, le a chave
                else -> throw IllegalArgumentException("Invalid AI model type specified in configuration. Valid values are 'OPENAI', 'GEMINI', 'NVIDIA', 'OPENAI-CLASSES', 'GEMINI-CLASSES' or 'NVIDIA-CLASSES'.")
            }
        }
    }
}
