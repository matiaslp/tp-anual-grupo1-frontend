import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import Helpers.LevDist;



public class levTest {
	String a,b,c,d,e;
	boolean resultado;
	@Before
	public void inicializar(){
		a = "Esto es una prueba";
		b = "Exto es una prueba";
		c = "Exto es una prueva";
		d = "Exto es una praeva";
	}
	
	
	@Test
	public void testSinDiferencias(){
		resultado = LevDist.calcularDistancia(a,a);
		Assert.assertTrue(resultado);
			
	}
	
	@Test
	public void test1Diferencia(){
		resultado = LevDist.calcularDistancia(a,b);
		Assert.assertTrue(resultado);
			
	}
	
	@Test
	public void test2Diferencias(){
		resultado = LevDist.calcularDistancia(a,c);
		Assert.assertFalse(resultado);
	}
	
	@Test
	public void test3Diferencias(){
		resultado = LevDist.calcularDistancia(a,d);
		Assert.assertFalse(resultado);
	}
	
	@Test
	public void testNull(){
		resultado = LevDist.calcularDistancia(a,e);
		Assert.assertFalse(resultado);
			
	}
}
