package cloud.zenixapp.zenix.configs.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;

@Log4j2
public final class HelpersLogs {

    private static final String RESPONSE_TIMER = "[%s:%s]: [Status: %s] => [Message: %s] => [Tempo de execução %dms]";
    private static final String RESPONSE = "[%s:%s]: [Status: %s] => [Message: %s]";

    private HelpersLogs() {
    }

    /**
     * <h2>
     *     Método auxiliar para logar resposta.
     * </h2>
     * @param entity Qual entidade utiliza esse log.
     * @param status Status Code da resposta.
     * @param message Mensagem da resposta.
     * @param inicio Tempo inicial de execução
     * @param fim Tempo final de execução
     */
    public static void logResponse(String camada, String entity, HttpStatus status, String message, long inicio, long fim){
        String mensagem = RESPONSE_TIMER.formatted(camada, entity, status, message, (fim - inicio));
        if (status.is2xxSuccessful()){
            log.info(mensagem);

        } else if (status.is4xxClientError() || status.is5xxServerError()){
            log.error(mensagem);

        } else {
            log.warn(mensagem);

        }
    }

    /**
     * <h2>
     *      Método auxiliar para logar resposta.
     * </h2>
     * <p>
     *     Sobrecarga do método {@link HelpersLogs#logResponse(String, String, HttpStatus, String, long, long) LogResponse}.
     *     Retirei os parâmetros de {@code inicio} e {@code fim}
     * </p>
     * @param entity Qual entidade utiliza esse log.
     * @param status Status Code da resposta.
     * @param message Mensagem da resposta.
     */
    public static void logResponse(String camada, String entity, HttpStatus status, String message){
        String mensagem = RESPONSE.formatted(camada, entity, status, message);
        if (status.is2xxSuccessful()){
            log.info(mensagem);

        } else if (status.is4xxClientError() || status.is5xxServerError()){
            log.error(mensagem);

        } else {
            log.warn(mensagem);

        }

    }

    /**
     * <h2>
     *     Método auxiliar para logar o início da classe e método no Service.
     * </h2>
     * @param objetivo O que o método faz.
     * @param classe Classe que possui o método.
     * @param metodo Método chamado.
     */
    public static void logInfoServices(String objetivo, String classe, String metodo){
        log.info("[SERVICE -> {}] : {}.{}", objetivo, classe, metodo);
    }

    /**
     * <h2>
     *     Método auxiliar para logar erros de exceção.
     * </h2>
     * @param logger Quem lançou a exceção.
     * @param metodo Método que lançou a exceção.
     */
    public static void logException(String logger, String metodo){
        log.error("Exceção lançada por: {}.{}", logger, metodo);
    }

    /**
     * <h2>
     *     Método auxiliar para logar o início da classe e método no Controller.
     * </h2>
     * @param metodoHttp Método HTTP - Exemplo: {@code POST}; {@code GET}; {@code DELETE}; {@code PUT};
     * @param classe Classe que é controladora.
     * @param metodo Método Java que executa a função.
     */
    public static void logInfoController(String metodoHttp, String classe, String metodo, String apiUrl){
        log.info("[CONTROLLER -> {}] : {}.{} => Endpoint: {/{}/atendimentos}", metodoHttp, classe, metodo, apiUrl);
    }

}
