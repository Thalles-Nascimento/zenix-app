package cloud.zenixapp.zenix;

import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.exceptions.UsuarioExcluidoException;
import cloud.zenixapp.zenix.configs.mappers.UsuarioMapper;
import cloud.zenixapp.zenix.models.dtos.requests.UsuarioRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.UsuarioResponseDTO;
import cloud.zenixapp.zenix.models.entities.Unidades;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.repositories.UnidadeRepository;
import cloud.zenixapp.zenix.repositories.UsuarioRepository;
import cloud.zenixapp.zenix.services.UnidadeService;
import cloud.zenixapp.zenix.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum.ADMIN;
import static cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@SpringBootTest
//@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTests {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UnidadeRepository unidadeRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private UnidadeService unidadeService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuarios usuarioAtivo;
    private Usuarios usuarioExcluido;
    private Unidades unidade;

    @BeforeEach
    void setUp() {
        unidade = new Unidades();
        unidade.setId(1L);
        unidade.setNomeUnidade("WN Barbearia");
        unidade.setEndereco("Manilha");
        unidade.setStatus(1);

        usuarioAtivo = new Usuarios();
        usuarioAtivo.setId(1L);
        usuarioAtivo.setNome("Thalles");
        usuarioAtivo.setEmail("thalles@gmail.com");
        usuarioAtivo.setStatus(1);
        usuarioAtivo.setUnidade(unidade);

        usuarioExcluido = new Usuarios();
        usuarioExcluido.setId(2L);
        usuarioExcluido.setNome("Hugo");
        usuarioExcluido.setEmail("hugo@gmail.com");
        usuarioExcluido.setStatus(-1);
        usuarioExcluido.setUnidade(unidade);
    }

    @Test
    @DisplayName("Deve deletar usuário ativo com sucesso")
    void deletarUsuario_quandoUsuarioAtivo_deveRetornarSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAtivo));

        SucessUsuarioResponseDTO resultado = usuarioService.deletarUsuario(1L);

        assertEquals(200, resultado.status());
        verify(usuarioRepository, times(1)).deleteLogico(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar usuário já excluído")
    void deletarUsuario_quandoUsuarioJaExcluido_deveLancarExcecao() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioExcluido));

        assertThrows(UsuarioExcluidoException.class, () -> {
            usuarioService.deletarUsuario(2L);
        });

        verify(usuarioRepository, never()).deleteLogico(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar usuário inexistente")
    void deletarUsuario_quandoUsuarioNaoExiste_deveLancarExcecao() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            usuarioService.deletarUsuario(99L);
        });
    }

    @Test
    @DisplayName("Deve reativar usuário excluído com sucesso")
    void ativarUsuario_quandoUsuarioExcluido_deveRetornarSucesso() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioExcluido));
        when(usuarioMapper.usuarioResponseDTO(usuarioExcluido)).thenReturn(new UsuarioResponseDTO(
                2L, "Hugo", "hugo@gmail.com", "12345678900", unidade, USER, -1, null
        ));

        SucessUsuarioResponseDTO resultado = usuarioService.ativarUsuario(2L);

        assertEquals(200, resultado.status());
        verify(usuarioRepository, times(1)).ativarUsuario(2L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao reativar usuário já ativo")
    void ativarUsuario_quandoUsuarioJaAtivo_deveLancarExcecao() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAtivo));

        assertThrows(UsuarioExcluidoException.class, () -> {
            usuarioService.ativarUsuario(1L);
        });

        verify(usuarioRepository, never()).ativarUsuario(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário excluído")
    void atualizarUsuario_quandoUsuarioExcluido_deveLancarExcecao() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioExcluido));

        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Hugo Novo", "hugo@gmail.com", "12345678900", 1L, "123456", USER);

        assertThrows(NotFoundException.class, () -> {
            usuarioService.atualizarUsuario(2L, dto);
        });
    }

    @Test
    @DisplayName("Deve atualizar unidade do usuário quando informada")
    void atualizarUsuario_quandoUnidadeInformada_deveAtualizarUnidade() {
        Unidades novaUnidade = new Unidades();
        novaUnidade.setId(3L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAtivo));
        when(unidadeRepository.findById(3L)).thenReturn(Optional.of(novaUnidade));
        when(usuarioRepository.save(any())).thenReturn(usuarioAtivo);
        when(usuarioMapper.usuarioResponseDTO(any())).thenReturn(
                new UsuarioResponseDTO(1L, "Thalles", "thalles@gmail.com", "12345678900", novaUnidade, ADMIN, 1, null)
        );

        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Thalles", "thalles@gmail.com", "12345678900", 3L, "123456", ADMIN);

        SucessUsuarioResponseDTO resultado = usuarioService.atualizarUsuario(1L, dto);

        assertEquals(200, resultado.status());
        assertEquals(3L, usuarioAtivo.getUnidade().getId());
    }

    @Test
    @DisplayName("Deve retornar null quando email já cadastrado")
    void registerUser_quandoEmailJaCadastrado_deveRetornarNull() {
        when(usuarioRepository.findByEmail("thalles@gmail.com")).thenReturn(usuarioAtivo);

        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Thalles", "thalles@gmail.com", "12345678900", 3L, "123456", ADMIN);

        SucessUsuarioResponseDTO resultado = usuarioService.registerUser(dto);

        assertNull(resultado);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve registrar usuário com sucesso quando email não existe")
    void registerUser_quandoEmailNaoExiste_deveRegistrarComSucesso() {
        when(usuarioRepository.findByEmail("novo@gmail.com")).thenReturn(null);
        when(unidadeService.listarUnidadeByIdCompleto(1L)).thenReturn(unidade);
        when(usuarioRepository.save(any())).thenReturn(usuarioAtivo);
        when(usuarioMapper.usuarioResponseDTO(any())).thenReturn(
                new UsuarioResponseDTO(1L, "Novo", "novo@gmail.com", "12345678900", unidade, USER, 1, null)
        );

        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Novo", "novo@gmail.com", "12345678900", 1L, "123456", ADMIN);

        SucessUsuarioResponseDTO resultado = usuarioService.registerUser(dto);

        assertNotNull(resultado);
        assertEquals(201, resultado.status());
        verify(usuarioRepository, times(1)).save(any());
    }

}
