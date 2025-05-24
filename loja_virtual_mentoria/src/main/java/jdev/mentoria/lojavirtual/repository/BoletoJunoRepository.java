package jdev.mentoria.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jdev.mentoria.lojavirtual.model.BoletoJuno;

//repository para CRUD do boleto no banco
@Repository
public interface BoletoJunoRepository extends JpaRepository<BoletoJuno, Long> {
	
	@Query("select b from BoletoJuno b where b.code = ?1")
	public BoletoJuno findByCode (String code);

	 //FORMA 1 QUITARBOLETO
	 //
	 //se na nosso BANCODEDADOS o BOLETO/PIX ta o quitado como false... 
	 //e a JUNO disse para nos q foi pago o PIX/BOLETO
	 //vamos chamar o metodo QUITARBOLETOBYID e passar
	 //o CODE do boleto q a JUNO disse q foi pago... e colocar como pago
	@Modifying(flushAutomatically = true)
	@Query(nativeQuery = true, value = "update boleto_juno set quitado = true where code = ?1")
	public void quitarBoleto(String code);
	
	 //FORMA 2 QUITARBOLETO
	 //
	 //se na nosso BANCODEDADOS o BOLETO/PIX ta o quitado como false... 
	 //e a JUNO disse para nos q foi pago o PIX/BOLETO
	 //vamos chamar o metodo QUITARBOLETOBYID e passar
	 //o ID do boleto q a JUNO disse q foi pago... e colocar como pago
	@Transactional
	@Modifying(flushAutomatically = true)
	@Query(nativeQuery = true, value = "update boleto_juno set quitado = true where id = ?1")
	public void quitarBoletoById(Long id);

	@Transactional
	@Modifying(flushAutomatically = true)
	@Query(nativeQuery = true, value = "delete from boleto_juno where code = ?1")
	public void deleteByCode(String code);

}
