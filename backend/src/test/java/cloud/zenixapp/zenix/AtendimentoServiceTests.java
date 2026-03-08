package cloud.zenixapp.zenix;

import cloud.zenixapp.zenix.configs.exceptions.AtendimentoExcluidoException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.zenix.models.dtos.requests.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SucessAtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.entities.Atendimento;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.repositories.AtendimentoRepository;
import cloud.zenixapp.zenix.repositories.UsuarioRepository;
import cloud.zenixapp.zenix.services.AtendimentoService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AtendimentoServiceTests {

    @Mock
    private AtendimentoRepository atendimentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AtendimentoMapper atendimentoMapper;

    @InjectMocks
    private AtendimentoService atendimentoService;

    private Usuarios usuarioLogado;
    private Atendimento atendimentoAtivo;
    private Atendimento atendimentoExcluido;

    @BeforeEach
    void setUp() {
        usuarioLogado = new Usuarios();
        usuarioLogado.setId(1L);
        usuarioLogado.setNome("Thalles");

        atendimentoAtivo = new Atendimento();
        atendimentoAtivo.setId(1L);
        atendimentoAtivo.setDescricao("Memphis Depay");
        atendimentoAtivo.setServico(List.of("Corte", "Sombrancelha"));
        atendimentoAtivo.setValor(50.0);
        atendimentoAtivo.setStatus(1);
        atendimentoAtivo.setUsuarios(usuarioLogado);

        atendimentoExcluido = new Atendimento();
        atendimentoExcluido.setId(2L);
        atendimentoExcluido.setDescricao("Nathan");
        atendimentoExcluido.setServico(List.of("Barba"));
        atendimentoExcluido.setValor(30.0);
        atendimentoExcluido.setStatus(-1);
        atendimentoExcluido.setUsuarios(usuarioLogado);
    }

    // Método auxiliar para mockar o SecurityContextHolder
    private void mockSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuarioLogado);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Deve inserir atendimento com sucesso")
    void inserirAtendimento_quandoDadosValidos_deveRetornarSucesso() {
        mockSecurityContext();
        when(usuarioRepository.getReferenceById(1L)).thenReturn(usuarioLogado);
        when(atendimentoRepository.save(any())).thenReturn(atendimentoAtivo);

        AtendimentoRequestDTO dto = new AtendimentoRequestDTO(
                "Memphis Depay", List.of("Corte", "Sombrancelha"), "PIX", 35.0
        );

        SucessAtendimentoResponseDTO resultado = atendimentoService.inserirAtendimento(dto);

        assertNotNull(resultado);
        assertEquals(201, resultado.status());
        verify(atendimentoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve deletar atendimento ativo com sucesso")
    void deletarAtendimento_quandoAtivo_deveRetornarSucesso() {
        when(atendimentoRepository.findById(1L)).thenReturn(Optional.of(atendimentoAtivo));

        SucessAtendimentoResponseDTO resultado = atendimentoService.deletarAtendimento(1L);

        assertEquals(200, resultado.status());
        verify(atendimentoRepository, times(1)).deleteLogico(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar atendimento já excluído")
    void deletarAtendimento_quandoJaExcluido_deveLancarExcecao() {
        when(atendimentoRepository.findById(2L)).thenReturn(Optional.of(atendimentoExcluido));

        assertThrows(AtendimentoExcluidoException.class, () -> {
            atendimentoService.deletarAtendimento(2L);
        });

        verify(atendimentoRepository, never()).deleteLogico(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar atendimento inexistente")
    void deletarAtendimento_quandoNaoExiste_deveLancarExcecao() {
        when(atendimentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            atendimentoService.deletarAtendimento(99L);
        });
    }

    @Test
    @DisplayName("Deve atualizar atendimento com sucesso")
    void atualizarAtendimento_quandoAtivo_deveRetornarSucesso() {
        mockSecurityContext();
        when(atendimentoRepository.findByUserById(1L, 1L)).thenReturn(Optional.of(atendimentoAtivo));

        AtendimentoRequestDTO dto = new AtendimentoRequestDTO(
                "Memphis Depay", List.of("Corte + Barba"), "CARTAO", 70.0
        );

        SucessAtendimentoResponseDTO resultado = atendimentoService.atualizarAtendimento(1L, dto);

        assertEquals(200, resultado.status());
        verify(atendimentoMapper, times(1)).atualizarAtendimento(atendimentoAtivo, dto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar atendimento excluído")
    void atualizarAtendimento_quandoExcluido_deveLancarExcecao() {
        mockSecurityContext();
        when(atendimentoRepository.findByUserById(1L, 2L)).thenReturn(Optional.of(atendimentoExcluido));

        AtendimentoRequestDTO dto = new AtendimentoRequestDTO(
                "Nathan", List.of("Barba"), "DINHEIRO", 30.0
        );

        assertThrows(NotFoundException.class, () -> {
            atendimentoService.atualizarAtendimento(2L, dto);
        });

        verify(atendimentoMapper, never()).atualizarAtendimento(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar atendimento inexistente")
    void atualizarAtendimento_quandoNaoExiste_deveLancarExcecao() {
        mockSecurityContext();
        when(atendimentoRepository.findByUserById(1L, 99L)).thenReturn(Optional.empty());

        AtendimentoRequestDTO dto = new AtendimentoRequestDTO(
                "Inexistente", List.of("Corte"), "PIX", 50.0
        );

        assertThrows(NotFoundException.class, () -> {
            atendimentoService.atualizarAtendimento(99L, dto);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar atendimento excluído por id")
    void listarAtendimentoPorId_quandoExcluido_deveLancarExcecao() {
        mockSecurityContext();

        AtendimentoResponseDTO dtoExcluido = new AtendimentoResponseDTO(
                2L, "Nathan", List.of("Barba"), 30.0, "DINHEIRO", "05/03/2026", -1
        );

        when(atendimentoRepository.findByUserById(1L, 2L)).thenReturn(Optional.of(atendimentoExcluido));
        when(atendimentoMapper.responseDTO(atendimentoExcluido)).thenReturn(dtoExcluido);

        assertThrows(NotFoundException.class, () -> {
            atendimentoService.listarAtendimentoPorId(2L);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar atendimento inexistente por id")
    void listarAtendimentoPorId_quandoNaoExiste_deveLancarExcecao() {
        mockSecurityContext();
        when(atendimentoRepository.findByUserById(1L, 99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            atendimentoService.listarAtendimentoPorId(99L);
        });
    }

    @Test
    @DisplayName("Deve retornar histórico de atendimentos do usuário")
    void listarHistorico_quandoExistemAtendimentos_deveRetornarLista() {
        when(atendimentoRepository.findByUser(1L)).thenReturn(List.of(atendimentoAtivo));
        when(atendimentoMapper.listResponseDTO(any())).thenReturn(List.of(
                new AtendimentoResponseDTO(1L, "Memphis Depay", List.of("Barba"), 50.0, "PIX", "05/03/2026", 1)
        ));

        var resultado = atendimentoService.listarHistorico(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Memphis Depay", resultado.get(0).descricao());
    }


}
