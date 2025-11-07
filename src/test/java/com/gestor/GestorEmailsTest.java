package com.gestor;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.ucp.gestor.BandejaType;
import com.ucp.gestor.Contacto;
import com.ucp.gestor.Email;
import com.ucp.gestor.Filtro;
import com.ucp.gestor.GestorEmails;

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
}
