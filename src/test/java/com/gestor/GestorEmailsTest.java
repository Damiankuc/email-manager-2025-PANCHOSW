package com.gestor;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import static com.gestor.BandejaType.NONLEIDO;


public class GestorEmailsTest {
    private GestorEmails gestor;
    private Contacto c1, c2;

    @Before
    public void setup() {
        gestor = new GestorEmails();
        c1 = gestor.crearContacto("A", "a@demo.com");
        c2 = gestor.crearContacto("B", "b@demo.com");
    }

    @Test
    public void testCrearYEnviarEmail() {
        Email e = gestor.crearEmail(c1, "Asunto", "Contenido", Arrays.asList(c2));
        assertTrue(e.isBorrador());
        gestor.enviar(e);
        assertFalse(e.isBorrador());
        List<Email> enviados = gestor.getTodosEnBandeja(BandejaType.ENVIADOS);
        assertEquals(1, enviados.size());
        List<Email> entrada = gestor.getTodosEnBandeja(BandejaType.ENTRADA);
        assertEquals(1, entrada.size());
    }

    @Test
    public void testFiltrosBasico() {
        Email e1 = gestor.crearEmail(c1, "UCP info", "contenido", Arrays.asList(c2));
        gestor.enviar(e1);
        Filtro f = new Filtro("buscar ucp", email ->
                email.getAsunto() != null && email.getAsunto().toLowerCase().contains("ucp")
        );
        List<Email> res = gestor.aplicarFiltroSobreBandeja(f, BandejaType.ENTRADA);
        assertEquals(1, res.size());
    }

    @Test
    public void testMarcarFavoritoYFavoritosBandeja() {
        Email e = gestor.crearEmail(c1, "Hola", "X", Arrays.asList(c2));
        gestor.enviar(e);
        gestor.marcarFavorito(e.getId(), true);
        List<Email> favs = gestor.getTodosEnBandeja(BandejaType.FAVORITOS);
        assertTrue(favs.contains(e));
    }

    @Test 
    public void TestEmailEnBorrador() {

        Email e = gestor.crearEmail(c1, "Borrador", "X", Arrays.asList(c2));
        List<Email> borradores = gestor.getTodosEnBandeja(BandejaType.BORRADORES);
        assertTrue(borradores.contains(e));
    }
    @Test
    public void TestVerificarCorreoEnBorradoresconFiltro() {
       List<Email> borradoresAntes = gestor.getTodosEnBandeja(BandejaType.BORRADORES);
         Email e = gestor.crearEmail(c1, "BorradorFiltro", "X", Arrays.asList(c2));
            Filtro f = new Filtro("filtro borrador", email ->
                    email.getAsunto() != null && email.getAsunto().toLowerCase().contains("borradorfiltro")
            );
        // Verificar que el email creado esté en borradores
        List<Email> borradoresDespues = gestor.getTodosEnBandeja(BandejaType.BORRADORES);
        assertTrue(borradoresDespues.contains(e));
        assertEquals(borradoresAntes.size() + 1, borradoresDespues.size());

        // Aplicar el filtro sobre borradores y verificar que encuentre el email
        List<Email> filtrados = gestor.aplicarFiltroSobreBandeja(f, BandejaType.BORRADORES);
        assertEquals(1, filtrados.size());
        assertTrue(filtrados.contains(e));
    }
    @Test 
    public void TestMarcarLeido() {
        Email e = gestor.crearEmail(c1, "Leido", "X", Arrays.asList(c2));
        gestor.enviar(e);
        gestor.marcarLeido(e.getId(), true);
        List<Email> leidos = gestor.getTodosEnBandeja(BandejaType.LEIDO);
        assertTrue(leidos.contains(e));
    }
    @Test 
    public void TestCorreoNoLeido() {
        Email e = gestor.crearEmail(c1, "No Leido", "X", Arrays.asList(c2));
        gestor.enviar(e);
        gestor.marcarLeido(e.getId(), false);
        List<Email> leidos = gestor.getTodosEnBandeja(BandejaType.LEIDO);
        assertFalse(leidos.contains(e));
    }
    @Test 
    public void TestVerificarCorreoNoLeidoconFiltro() {
        List<Email> noLeidosAntes = gestor.getTodosEnBandeja(NONLEIDO);
        Email e = gestor.crearEmail(c1, "NoLeidoFiltro", "X", Arrays.asList(c2));
        gestor.enviar(e);
        Filtro f = new Filtro("filtro no leido", email ->
                email.getAsunto() != null && email.getAsunto().toLowerCase().contains("noleidofiltro")
        );
        // Verificar que el email creado esté en no leidos
        List<Email> noLeidosDespues = gestor.getTodosEnBandeja(BandejaType.NONLEIDO);
        assertTrue(noLeidosDespues.contains(e));
        assertEquals(noLeidosAntes.size() + 1, noLeidosDespues.size());

        // Aplicar el filtro sobre no leidos y verificar que encuentre el email
        List<Email> filtrados = gestor.aplicarFiltroSobreBandeja(f, BandejaType.NONLEIDO);
        assertEquals(1, filtrados.size());
        assertTrue(filtrados.contains(e));
    }
}
    