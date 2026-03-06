package cloud.zenixapp.zenix;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.exceptions.UnidadeExcluidoException;
import cloud.zenixapp.zenix.configs.mappers.UnidadeMapper;
import cloud.zenixapp.zenix.models.dtos.requests.UnidadeRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.*;
import cloud.zenixapp.zenix.models.entities.Unidades;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;
import cloud.zenixapp.zenix.repositories.UnidadeRepository;
import cloud.zenixapp.zenix.services.UnidadeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnidadeServiceTest {

    @Mock
    private UnidadeRepository unidadeRepository;

    @Mock
    private UnidadeMapper unidadeMapper;

    @InjectMocks
    private UnidadeService unidadeService;

    private Unidades unidadeAtiva;
    private Unidades unidadeExcluida;
    private Usuarios barbeiro;

    @BeforeEach
    void setUp() {
        barbeiro = new Usuarios();
        barbeiro.setId(1L);
        barbeiro.setNome("Thalles");
        barbeiro.setEmail("thalles@gmail.com");
        barbeiro.setGrupo(UsuariosRoleEnum.USER);
        barbeiro.setStatus(1);

        unidadeAtiva = new Unidades();
        unidadeAtiva.setId(1L);
        unidadeAtiva.setNomeUnidade("WN Barbearia Venda das Pedras");
        unidadeAtiva.setEndereco("Venda das Pedras");
        unidadeAtiva.setStatus(1);
        unidadeAtiva.setUsuarios(List.of(barbeiro));

        unidadeExcluida = new Unidades();
        unidadeExcluida.setId(2L);
        unidadeExcluida.setNomeUnidade("WN Barbearia Manilha");
        unidadeExcluida.setEndereco("Manilha");
        unidadeExcluida.setStatus(-1);
        unidadeExcluida.setUsuarios(List.of());
    }

    // ─────────────────────────────────────────
    // TESTES - inserirUnidade
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Deve inserir unidade com sucesso")
    void inserirUnidade_quandoDadosValidos_deveRetornarSucesso() {
        UnidadeRequestDTO dto = new UnidadeRequestDTO("WN Barbearia Centro", "Centro");

        when(unidadeMapper.toUnidade(dto)).thenReturn(unidadeAtiva);
        when(unidadeRepository.save(unidadeAtiva)).thenReturn(unidadeAtiva);

        SuccessUnidadeResponseDTO resultado = unidadeService.inserirUnidade(dto);

        assertNotNull(resultado);
        assertEquals(200, resultado.status());
        assertEquals("Unidade inserida com sucesso", resultado.message());
        verify(unidadeRepository, times(1)).save(unidadeAtiva);
    }

    // ─────────────────────────────────────────
    // TESTES - listarUnidadeById
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar unidade ativa por id")
    void listarUnidadeById_quandoAtiva_deveRetornarUnidade() {
        UnidadeResponseDTO dto = new UnidadeResponseDTO(1L, "WN Barbearia Venda das Pedras", "Venda das Pedras", 1);

        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidadeAtiva));
        when(unidadeMapper.toDTO(unidadeAtiva)).thenReturn(dto);

        UnidadeResponseDTO resultado = unidadeService.listarUnidadeById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("WN Barbearia Venda das Pedras", resultado.nomeUnidade());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar unidade excluída")
    void listarUnidadeById_quandoExcluida_deveLancarExcecao() {
        when(unidadeRepository.findById(2L)).thenReturn(Optional.of(unidadeExcluida));

        assertThrows(UnidadeExcluidoException.class, () -> {
            unidadeService.listarUnidadeById(2L);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar unidade inexistente")
    void listarUnidadeById_quandoNaoExiste_deveLancarExcecao() {
        when(unidadeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            unidadeService.listarUnidadeById(99L);
        });
    }

    // ─────────────────────────────────────────
    // TESTES - listarUnidadesByIdUsuario
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar unidade com apenas barbeiros (sem admins)")
    void listarUnidadesByIdUsuario_deveRetornarApenasUsuariosUSER() {
        // Adiciona um admin que não deve aparecer
        Usuarios admin = new Usuarios();
        admin.setId(2L);
        admin.setNome("Matheus");
        admin.setEmail("matheus@gmail.com");
        admin.setGrupo(UsuariosRoleEnum.ADMIN);
        admin.setStatus(1);

        unidadeAtiva.setUsuarios(List.of(barbeiro, admin));
        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidadeAtiva));

        UnidadeUserResponseDTO resultado = unidadeService.listarUnidadesByIdUsuario(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.usuarios().size()); // ← só o barbeiro
        assertEquals(UsuariosRoleEnum.USER, resultado.usuarios().getFirst().grupo());
        assertEquals("Thalles", resultado.usuarios().getFirst().nome());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuários de unidade inexistente")
    void listarUnidadesByIdUsuario_quandoNaoExiste_deveLancarExcecao() {
        when(unidadeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            unidadeService.listarUnidadesByIdUsuario(99L);
        });
    }

    // ─────────────────────────────────────────
    // TESTES - listarUnidadeByIdCompleto
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar entidade unidade ativa completa")
    void listarUnidadeByIdCompleto_quandoAtiva_deveRetornarUnidade() {
        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidadeAtiva));

        Unidades resultado = unidadeService.listarUnidadeByIdCompleto(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("WN Barbearia Venda das Pedras", resultado.getNomeUnidade());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar entidade de unidade excluída")
    void listarUnidadeByIdCompleto_quandoExcluida_deveLancarExcecao() {
        when(unidadeRepository.findById(2L)).thenReturn(Optional.of(unidadeExcluida));

        assertThrows(UnidadeExcluidoException.class, () -> {
            unidadeService.listarUnidadeByIdCompleto(2L);
        });
    }

    // ─────────────────────────────────────────
    // TESTES - atualizarUnidade
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Deve atualizar unidade ativa com sucesso")
    void atualizarUnidade_quandoAtiva_deveRetornarSucesso() {
        UnidadeRequestDTO dto = new UnidadeRequestDTO("WN Barbearia Venda das Pedras Atualizada", "Venda das Pedras");

        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidadeAtiva));

        SuccessUnidadeResponseDTO resultado = unidadeService.atualizarUnidade(1L, dto);

        assertNotNull(resultado);
        assertEquals(200, resultado.status());
        assertEquals("Unidade atualizada com sucesso", resultado.message());
        verify(unidadeMapper, times(1)).atualizarUnidade(unidadeAtiva, dto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar unidade excluída")
    void atualizarUnidade_quandoExcluida_deveLancarExcecao() {
        UnidadeRequestDTO dto = new UnidadeRequestDTO("Nome", "Endereco");

        when(unidadeRepository.findById(2L)).thenReturn(Optional.of(unidadeExcluida));

        assertThrows(NotFoundException.class, () -> {
            unidadeService.atualizarUnidade(2L, dto);
        });

        verify(unidadeMapper, never()).atualizarUnidade(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar unidade inexistente")
    void atualizarUnidade_quandoNaoExiste_deveLancarExcecao() {
        UnidadeRequestDTO dto = new UnidadeRequestDTO("Nome", "Endereco");

        when(unidadeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            unidadeService.atualizarUnidade(99L, dto);
        });
    }

    // ─────────────────────────────────────────
    // TESTES - deletarUnidade
    // ─────────────────────────────────────────

    @Test
    @DisplayName("Deve deletar unidade ativa com sucesso")
    void deletarUnidade_quandoAtiva_deveRetornarSucesso() {
        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidadeAtiva));

        SuccessUnidadeDeleteResponseDTO resultado = unidadeService.deletarUnidade(1L);

        assertNotNull(resultado);
        assertEquals(200, resultado.status());
        assertEquals("WN Barbearia Venda das Pedras", resultado.nomeUnidade());
        verify(unidadeRepository, times(1)).deleteLogico(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar unidade já excluída")
    void deletarUnidade_quandoJaExcluida_deveLancarExcecao() {
        when(unidadeRepository.findById(2L)).thenReturn(Optional.of(unidadeExcluida));

        assertThrows(UnidadeExcluidoException.class, () -> {
            unidadeService.deletarUnidade(2L);
        });

        verify(unidadeRepository, never()).deleteLogico(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar unidade inexistente")
    void deletarUnidade_quandoNaoExiste_deveLancarExcecao() {
        when(unidadeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            unidadeService.deletarUnidade(99L);
        });
    }
}
