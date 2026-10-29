package com.delegrego.api_alunos_testes.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.delegrego.api_alunos_testes.dto.AlunoRequest;
import com.delegrego.api_alunos_testes.dto.AlunoResponse;
import com.delegrego.api_alunos_testes.exception.AlunoNaoEncontradoException;
import com.delegrego.api_alunos_testes.exception.EmailCadastradoException;
import com.delegrego.api_alunos_testes.model.Aluno;
import com.delegrego.api_alunos_testes.repository.AlunoRepository;

@Service
public class AlunoService {

	private final AlunoRepository alunoRepository;

	public AlunoService(AlunoRepository alunoRepository) {
		this.alunoRepository = alunoRepository;
	}
	
	// LISTAR ALUNOS

	public List<AlunoResponse> listarAlunos() {
		
		List<Aluno> alunos = alunoRepository.findAll(); // EXTERNO
		List<AlunoResponse> respostas = new ArrayList<>();

		for (Aluno aluno : alunos) {
			respostas.add(new AlunoResponse(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getDataNascimento(),
					aluno.getMedia()));
		}

		return respostas;
	}
	
	// OBTER ALUNO POR ID

	public AlunoResponse obterAlunoPorId(Long id) {
		Aluno aluno = alunoRepository.findById(id) // EXTERNO
				.orElseThrow(() -> new AlunoNaoEncontradoException("Aluno não encontrado"));

		return new AlunoResponse(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getDataNascimento(),
				aluno.getMedia());
	}
	
	// CADASTRAR ALUNO

	public AlunoResponse cadastrarAluno(AlunoRequest request) {

		if (alunoRepository.existsByEmailIgnoreCase(request.getEmail())) { // EXTERNO
			throw new EmailCadastradoException("Email já cadastrado");
		}

		Aluno aluno = new Aluno();
		aluno.setNome(request.getNome());
		aluno.setEmail(request.getEmail());
		aluno.setSenha(request.getSenha());
		aluno.setDataNascimento(request.getDataNascimento());
		aluno.setMedia(request.getMedia());

		Aluno alunoCadastrado = alunoRepository.save(aluno); // EXTERNO

		return new AlunoResponse(alunoCadastrado.getId(), alunoCadastrado.getNome(), alunoCadastrado.getEmail(),
				alunoCadastrado.getDataNascimento(), alunoCadastrado.getMedia());
	}
	
	// ATUALIZAR ALUNO

	public AlunoResponse atualizarAluno(Long id, AlunoRequest request) {

		Aluno aluno = alunoRepository.findById(id) // EXTERNO
				.orElseThrow(() -> new AlunoNaoEncontradoException("Aluno não encontrado"));

		if (alunoRepository.existsByEmailIgnoreCaseAndIdNot(request.getEmail(), id)) { // EXTERNO
			throw new EmailCadastradoException("Email já cadastrado");
		}

		aluno.setNome(request.getNome());
		aluno.setEmail(request.getEmail());
		aluno.setSenha(request.getSenha());
		aluno.setDataNascimento(request.getDataNascimento());
		aluno.setMedia(request.getMedia());

		Aluno alunoAtualizado = alunoRepository.save(aluno); // EXTERnO

		return new AlunoResponse(alunoAtualizado.getId(), alunoAtualizado.getNome(), alunoAtualizado.getEmail(),
				alunoAtualizado.getDataNascimento(), alunoAtualizado.getMedia());
	}
	
	
	
	// EXCLUIR ALUNO

	public void excluirAluno(Long id) {
		alunoRepository.deleteById(id); // EXTERNO
	}

}