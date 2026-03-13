package cloud.zenixapp.zenix.services;

import cloud.zenixapp.zenix.configs.exceptions.AtendimentoExcluidoException;
import cloud.zenixapp.zenix.configs.exceptions.NotFoundException;
import cloud.zenixapp.zenix.configs.mappers.AtendimentoMapper;
import cloud.zenixapp.zenix.models.dtos.requests.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.dtos.responses.AtendimentoAdminResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.AtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.SucessAtendimentoResponseDTO;
import cloud.zenixapp.zenix.models.entities.Atendimento;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.repositories.AtendimentoRepository;
import cloud.zenixapp.zenix.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AtendimentoService {

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    private final DateTimeFormatter current_date = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AtendimentoMapper atendimentoMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public SucessAtendimentoResponseDTO inserirAtendimento(AtendimentoRequestDTO atendimentoDTO){
        Usuarios userAuth = (Usuarios) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuarios user = usuarioRepository.getReferenceById(userAuth.getId());
        Atendimento atendimento = new Atendimento();

        atendimento.setDescricao(atendimentoDTO.descricao());
        atendimento.setServico(atendimentoDTO.servico());
        atendimento.setValor(atendimentoDTO.valor());
        atendimento.setFormaPagamento(atendimentoDTO.formaPagamento());
        atendimento.setObservacao(atendimentoDTO.observacao());
        atendimento.setDate(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).format(current_date));
        atendimento.setUsuarios(user);

        atendimentoRepository.save(atendimento);

        return new SucessAtendimentoResponseDTO(
                HttpStatus.CREATED.value(),
                "Atendimento inserido com sucesso!"
        );
    }

    public List<AtendimentoResponseDTO> listarAtendimentos(){
        Usuarios user = (Usuarios) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return atendimentoMapper.listResponseDTO(atendimentoRepository.findByUserDate(user.getId(), LocalDateTime.now().format(current_date)));
    }

    public List<AtendimentoAdminResponseDTO> listarTodosAtendimentos(){
        return atendimentoRepository
                .findAll()
                .stream()
                .map(a -> new AtendimentoAdminResponseDTO(
                        a.getId(),
                        a.getDescricao(),
                        a.getServico(),
                        a.getValor(),
                        a.getFormaPagamento(),
                        a.getDate(),
                        a.getStatus(),
                        a.getObservacao(),
                        a.getUsuarios().getNome()
                ))
                .toList();
    }

    public AtendimentoResponseDTO listarAtendimentoPorId(Long id){
        Usuarios user = (Usuarios) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return atendimentoRepository.findByUserById(user.getId(), id)
                .map((atendimento -> {
                    AtendimentoResponseDTO atendimentoResponseDTO = atendimentoMapper.responseDTO(atendimento);
                    if(atendimentoResponseDTO.status() == -1){
                        throw new NotFoundException("Atendimento foi excluído!");
                    }
                    return atendimentoResponseDTO;

                }))
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado!"));
    }

    public List<AtendimentoResponseDTO> listarHistorico(Long userId){
        return atendimentoMapper.listResponseDTO(
                atendimentoRepository.findByUser(userId)
        );
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SucessAtendimentoResponseDTO deletarAtendimento(Long id) {
        return atendimentoRepository.findById(id)
                .map(atendimento -> {
                    if (atendimento.getStatus() == -1) {
                        throw new AtendimentoExcluidoException("Atendimento já foi excluído!");

                    }

                    atendimentoRepository.deleteLogico(id);
                    return new SucessAtendimentoResponseDTO(
                            HttpStatus.OK.value(),
                            "Atendimento excluído com sucesso!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado!"));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SucessAtendimentoResponseDTO atualizarAtendimentoPorUsuario(Long id, AtendimentoRequestDTO atendimentoRequestDTO) throws NotFoundException {
        Usuarios user = (Usuarios) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return atendimentoRepository.findByUserById(user.getId(), id)
                .map(atendimento -> {
                    if(atendimento.getStatus() == -1){
                        throw new NotFoundException("Não é possível atualizar um atendimento excluído!");
                    }

                    atendimentoMapper.atualizarAtendimento(atendimento, atendimentoRequestDTO);
                    return new SucessAtendimentoResponseDTO(
                            HttpStatus.OK.value(),
                            "Atendimento atualizado com sucesso!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado!"));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SucessAtendimentoResponseDTO atualizarAtendimento(Long id, AtendimentoRequestDTO atendimentoRequestDTO) throws NotFoundException {
        return atendimentoRepository.findByIdAtendimento(id)
                .map(atendimento -> {
                    if(atendimento.getStatus() == -1){
                        throw new NotFoundException("Não é possível atualizar um atendimento excluído!");
                    }

                    atendimentoMapper.atualizarAtendimento(atendimento, atendimentoRequestDTO);
                    return new SucessAtendimentoResponseDTO(
                            HttpStatus.OK.value(),
                            "Atendimento atualizado com sucesso!"
                    );

                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado!"));
    }

    @Transactional
    public SucessAtendimentoResponseDTO ativarAtendimento(Long id){
        return atendimentoRepository.findById(id)
                .map(atendimento -> {
                    if(atendimento.getStatus() != -1){
                        throw new AtendimentoExcluidoException("Atendimento já está ativo!");
                    }
                    atendimentoRepository.ativarAtendimento(id);
                    return new SucessAtendimentoResponseDTO(
                            HttpStatus.OK.value(),
                            "Atendimento ativado com sucesso"
                    );
                })
                .orElseThrow(() -> new NotFoundException("Atendimento não encontrado!"));
    }

}
