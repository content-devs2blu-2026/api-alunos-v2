package com.delegrego.api_alunos_testes.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.delegrego.api_alunos_testes.dto.AlunoRequest;
import com.delegrego.api_alunos_testes.dto.AlunoResponse;
import com.delegrego.api_alunos_testes.exception.AlunoNaoEncontradoException;
import com.delegrego.api_alunos_testes.exception.EmailCadastradoException;
import com.delegrego.api_alunos_testes.model.Aluno;
import com.delegrego.api_alunos_testes.repository.AlunoRepository;

@ExtendWith(MockitoExtension.class)
public class AlunoServiceTest {
	
		// DEPENDÊNCIAS
		
		@InjectMocks
		AlunoService service;
		
		@Mock
		AlunoRepository repository;
		
		// LISTAR ALUNOS
		
		@Test
		void deveListarAlunos() {
			// Arrange
			
			Aluno primeiroAluno = new Aluno (1L, "Gabrielly", "gaby@gmail.com", "12345", LocalDate.of(2010, 06, 17), 10);
			Aluno segundoAluno = new Aluno (2L, "Gabriela", "gabriela@gmail.com", "12345", LocalDate.of(2010, 06, 17), 9);
			
				// Stub (PARTE)
			
				Mockito.when(repository.findAll())
					.thenReturn(List.of(primeiroAluno, segundoAluno));
			
			
			// Act
				
				List<AlunoResponse> listaAlunos = service.listarAlunos();
			
			// Assert
				
				Assertions.assertThat(listaAlunos).hasSize(2);
		}
		
		// OBTER ALUNO POR ID
		
		@Test
		void deveObterAlunoPeloId() {
			
			// Arrange
			
			Long idExistente = 1L;
			Aluno aluno = new Aluno(idExistente, "Gabrielly", "gaby@gmail.com", "12345", LocalDate.of(2010, 06, 17), 10);
			
				// Stub (PARTE)
			
				Mockito.when(repository.findById(idExistente))
					.thenReturn(Optional.of(aluno));
				
			// Act
			
			AlunoResponse response = service.obterAlunoPorId(idExistente);
			
			// Assert
			
			Assertions.assertThat(response).isNotNull();
			Assertions.assertThat(response.getId()).isEqualTo(idExistente);
			Assertions.assertThat(response.getNome()).isEqualTo("Gabrielly");
			Assertions.assertThat(response.getEmail()).isEqualTo("gaby@gmail.com");
			
		}
		
		@Test
		void deveRetornarExcecaoQuandoAlunoNaoEncontrado() {
			
			// Arrange
			
			Long idInexistente = 99L;
			
				// Stub (PARTE)
				
				Mockito.when(repository.findById(idInexistente))
					.thenReturn(Optional.empty());
			
			// Act & Assert
			
			Assertions.assertThatThrownBy(() -> service.obterAlunoPorId(idInexistente))
				.isInstanceOf(AlunoNaoEncontradoException.class)
				.hasMessageContaining("Aluno não encontrado");
			
		}
		
		// CADASTRAR ALUNO
		
		@Test
		void deveCadastrarERetornarAluno() {
			
			// Arrange
			
			AlunoRequest alunoRequest = new AlunoRequest("Gabrielly", "gaby@gmail.com", "12345", LocalDate.of(2010, 06, 17), 10);
			
				// Stub (PARTE)
			
			Mockito.when(repository.existsByEmailIgnoreCase(alunoRequest.getEmail()))
				.thenReturn(false);
			
			Aluno aluno = new Aluno (
					1L, 
					alunoRequest.getNome(), 
					alunoRequest.getEmail(), 
					alunoRequest.getSenha(), 
					alunoRequest.getDataNascimento(),
					alunoRequest.getMedia()
			);
			
			Mockito.when(repository.save(Mockito.any(Aluno.class)))
				.thenReturn(aluno);
			
			
			// Act
			
			AlunoResponse alunoSalvo = service.cadastrarAluno(alunoRequest);
			
			// Assert
			
			Assertions.assertThat(alunoSalvo).isNotNull();
			Assertions.assertThat(alunoSalvo.getId()).isGreaterThan(0);
		}
		
		// ATUALIZAR ALUNO
		
		@Test
		void deveAtualizarAlunoComSucesso() {
			// Arrange
			
			Long idExistente = 1L;
	        AlunoRequest request = new AlunoRequest("Gabrielly Bublitz", "gaby.novo@gmail.com", "98765", LocalDate.of(2010, 06, 17), 10);
			
	        Aluno alunoAntigo = new Aluno(idExistente, "Gabrielly", "gaby@gmail.com", "12345", LocalDate.of(2010, 06, 17), 10);
	        Aluno alunoAtualizado = new Aluno(idExistente, request.getNome(), request.getEmail(), request.getSenha(), request.getDataNascimento(), request.getMedia());
	        
				// Stub
	        
	        	Mockito.when(repository.findById(idExistente))
	        		.thenReturn(Optional.of(alunoAntigo));
	        	
	        	Mockito.when(repository.existsByEmailIgnoreCaseAndIdNot(request.getEmail(), idExistente))
	        		.thenReturn(false);
	        	
	        	Mockito.when(repository.save(Mockito.any(Aluno.class)))
	        		.thenReturn(alunoAtualizado);
			
			// Act
	        	
	        AlunoResponse response = service.atualizarAluno(idExistente, request);
			
			// Assert
	        	
	        Assertions.assertThat(response).isNotNull();
	        Assertions.assertThat(response.getNome()).isEqualTo("Gabrielly Bublitz");
	        Assertions.assertThat(response.getEmail()).isEqualTo("gaby.novo@gmail.com");
	        
	     // CHAMADO 1 VEZ
	        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Aluno.class));
		}
		
		@Test
		void deveRetornarExcecaoQuandoAtualizarAlunoInexistente() {
			
			// Arrange
			
			Long idInexistente = 99L;
			AlunoRequest request = new AlunoRequest("Gabrielly Bublitz", "gaby.novo@gmail.com", "98765", LocalDate.of(2010, 06, 17), 10);
			
				// Stub
			
				Mockito.when(repository.findById(idInexistente))
					.thenReturn(Optional.empty());
			
			// Act & Assert
				
			Assertions.assertThatThrownBy(() -> service.atualizarAluno(idInexistente, request))
				.isInstanceOf(AlunoNaoEncontradoException.class)
				.hasMessageContaining("Aluno não encontrado");
			
			// NUNCA CHAMADO
	        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Aluno.class));
	        
		}
		
		@Test
	    void deveLancarExcecaoQuandoEmailJaCadastradoPorOutroAluno() {
			
	        // Arrange
			
	        Long idExistente = 1L;
	        
	        AlunoRequest request = new AlunoRequest("Gabrielly", "email.duplicado@gmail.com", "12345", LocalDate.of(2010, 06, 17), 10);
	        Aluno alunoExistente = new Aluno(idExistente, "Gabrielly", "gaby@gmail.com", "12345", LocalDate.of(2010, 06, 17), 10);
	        
	        	// Stub
	        	Mockito.when(repository.findById(idExistente))
	        		.thenReturn(Optional.of(alunoExistente));
	        	
	        	Mockito.when(repository.existsByEmailIgnoreCaseAndIdNot(request.getEmail(), idExistente))
	        		.thenReturn(true);

	        // Act & Assert
	        	
	        Assertions.assertThatThrownBy(() -> service.atualizarAluno(idExistente, request))
	                .isInstanceOf(EmailCadastradoException.class)
	                .hasMessageContaining("Email já cadastrado");

	        // EXECUÇÃO TRAVOU - NUNCA CHAMADO
	        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Aluno.class));
	    }
		
		// EXCLUIR ALUNO
		
		@Test
		void deveExcluirAluno() {
			// Arrange
			Long idExistente = 1L;
			
			// Act
			
			service.excluirAluno(idExistente);
			
			// Assert
			
			Mockito.verify(repository, Mockito.times(1)).deleteById(idExistente);
		}
		

	
}
