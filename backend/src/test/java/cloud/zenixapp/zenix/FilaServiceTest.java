package cloud.zenixapp.zenix;

import cloud.zenixapp.zenix.configs.exceptions.FilaException;
import cloud.zenixapp.zenix.configs.mappers.FilaMapper;
import cloud.zenixapp.zenix.models.dtos.requests.FilaRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.FilaResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SucessFilaResponseDTO;
import cloud.zenixapp.zenix.models.entities.Fila;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.repositories.FilaAtendimentoRepository;
import cloud.zenixapp.zenix.services.FilaService;
import cloud.zenixapp.zenix.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static cloud.zenixapp.zenix.models.enums.StatusFilaEnum.AGUARDANDO;
import static cloud.zenixapp.zenix.models.enums.StatusFilaEnum.EM_ATENDIMENTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilaServiceTest {

    @Mock
    private FilaAtendimentoRepository filaRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private FilaMapper filaMapper;

    @InjectMocks
    private FilaService filaService;

    private Usuarios barbeiro;
    private Fila filaAguardando;
    private Fila filaEmAtendimento;
    private Fila filaFinalizada;

    @BeforeEach
    void setUp() {
        barbeiro = new Usuarios();
        barbeiro.setId(1L);
        barbeiro.setNome("Thalles");

        filaAguardando = new Fila();
        filaAguardando.setId(1L);
        filaAguardando.setNomeCliente("Memphis Depay");
        filaAguardando.setServico(List.of("Corte"));
        filaAguardando.setFormaPagamento("PIX");
        filaAguardando.setTelefoneCliente("+5521999999999");
        filaAguardando.setStatus(AGUARDANDO);
        filaAguardando.setUsuario(barbeiro);

        filaEmAtendimento = new Fila();
        filaEmAtendimento.setId(2L);
        filaEmAtendimento.setNomeCliente("Nathan");
        filaEmAtendimento.setServico(List.of("Barba"));
        filaEmAtendimento.setFormaPagamento("DINHEIRO");
        filaEmAtendimento.setStatus(EM_ATENDIMENTO);
        filaEmAtendimento.setUsuario(barbeiro);

        filaFinalizada = new Fila();
        filaFinalizada.setId(3L);
        filaFinalizada.setNomeCliente("Gustavo");
        filaFinalizada.setServico(List.of("Corte + Barba"));
        filaFinalizada.setFormaPagamento("CARTAO");
        filaFinalizada.setStatus(AGUARDANDO);
        filaFinalizada.setUsuario(barbeiro);
    }

    private void mockSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(barbeiro);
        SecurityContextHolder.setContext(securityContext);
    }

    // ─────────────────────────────────────────
    // TESTES - inserirAtendimentoFila
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Deve inserir cliente na fila com sucesso")
    void inserirAtendimentoFila_quandoDadosValidos_deveRetornarSucesso() throws java.sql.SQLIntegrityConstraintViolationException {
        when(usuarioService.getUsuarioById(1L)).thenReturn(barbeiro);
        when(filaRepository.save(any())).thenReturn(filaAguardando);

        FilaRequestDTO dto = new FilaRequestDTO(
                "Memphis Depay", List.of("Corte"), "+5521999999999", "PIX", 1L
        );

        SucessFilaResponseDTO resultado = filaService.inserirAtendimentoFila(dto);

        assertNotNull(resultado);
        assertEquals(AGUARDANDO, resultado.status());
        verify(filaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve associar o barbeiro correto ao inserir na fila")
    void inserirAtendimentoFila_deveBuscarBarbeiroCorreto() throws java.sql.SQLIntegrityConstraintViolationException {
        when(usuarioService.getUsuarioById(1L)).thenReturn(barbeiro);
        when(filaRepository.save(any())).thenReturn(filaAguardando);

        FilaRequestDTO dto = new FilaRequestDTO(
                "Memphis Depay", List.of("Corte"), "+5521999999999", "PIX", 1L
        );

        filaService.inserirAtendimentoFila(dto);

        verify(usuarioService, times(1)).getUsuarioById(1L);
    }

    // ─────────────────────────────────────────
    // TESTES - atualizarAtendimentoFila (chamar próximo)
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Deve chamar cliente aguardando com sucesso")
    void atualizarAtendimentoFila_quandoAguardando_deveRetornarSucesso() {
        when(filaRepository.findById(1L)).thenReturn(Optional.of(filaAguardando));

        SucessFilaResponseDTO resultado = filaService.chamarCliente(1L);

        assertNotNull(resultado);
        assertEquals("Memphis Depay", resultado.nomeCliente());
        verify(filaRepository, times(1)).paraAtendimento(1L);
        verify(filaRepository, times(1)).marcarHoraInicio(eq(1L), any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao chamar cliente já em atendimento")
    void atualizarAtendimentoFila_quandoEmAtendimento_deveLancarExcecao() {
        when(filaRepository.findById(2L)).thenReturn(Optional.of(filaEmAtendimento));

        assertThrows(FilaException.class, () -> {
            filaService.chamarCliente(2L);
        });

        verify(filaRepository, never()).paraAtendimento(any());
        verify(filaRepository, never()).marcarHoraInicio(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao chamar cliente inexistente")
    void atualizarAtendimentoFila_quandoNaoExiste_deveLancarExcecao() {
        when(filaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> {
            filaService.chamarCliente(99L);
        });
    }

    // ─────────────────────────────────────────
    // TESTES - finalizarAtendimento
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Deve finalizar atendimento em andamento com sucesso")
    void finalizarAtendimento_quandoEmAtendimento_deveRetornarSucesso() {
        when(filaRepository.findById(2L)).thenReturn(Optional.of(filaEmAtendimento));

        SucessFilaResponseDTO resultado = filaService.finalizarAtendimento(2L);

        assertNotNull(resultado);
        assertEquals("Nathan", resultado.nomeCliente());
        verify(filaRepository, times(1)).finalizarAtendimentoFila(2L);
        verify(filaRepository, times(1)).marcarHoraFinal(eq(2L), any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao finalizar cliente aguardando")
    void finalizarAtendimento_quandoAguardando_deveLancarExcecao() {
        when(filaRepository.findById(1L)).thenReturn(Optional.of(filaAguardando));

        assertThrows(FilaException.class, () -> {
            filaService.finalizarAtendimento(1L);
        });

        verify(filaRepository, never()).finalizarAtendimentoFila(any());
        verify(filaRepository, never()).marcarHoraFinal(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao finalizar cliente inexistente")
    void finalizarAtendimento_quandoNaoExiste_deveLancarExcecao() {
        when(filaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> {
            filaService.finalizarAtendimento(99L);
        });
    }

    // ─────────────────────────────────────────
    // TESTES - getFilasByUser
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar fila do barbeiro logado")
    void getFilasByUser_quandoBarbeiroLogado_deveRetornarFila() {
        mockSecurityContext();

        List<FilaResponseDTO> filaEsperada = List.of(
                new FilaResponseDTO(1L, "Memphis Depay", List.of("Corte"), "PIX", null, AGUARDANDO)
        );

        when(filaRepository.findByUser(1L)).thenReturn(List.of(filaAguardando));
        when(filaMapper.toListFilaDTO(any())).thenReturn(filaEsperada);

        List<FilaResponseDTO> resultado = filaService.getFilasByUser();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Memphis Depay", resultado.get(0).nomeCliente());
        verify(filaRepository, times(1)).findByUser(1L);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando barbeiro não tem clientes na fila")
    void getFilasByUser_quandoFilaVazia_deveRetornarListaVazia() {
        mockSecurityContext();

        when(filaRepository.findByUser(1L)).thenReturn(List.of());
        when(filaMapper.toListFilaDTO(any())).thenReturn(List.of());

        List<FilaResponseDTO> resultado = filaService.getFilasByUser();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }
}
