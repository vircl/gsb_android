/**
 * Fichier définissant la classe ApiException
 *
 * @author Virginie CLAUDE
 */
package fr.cned.emdsgil.suividevosfrais;

/**
 * Classe ApiException
 */
public class ApiException extends Exception {
    /**
     * Constructeur
     */
    public ApiException() {
        super();
    }

    /**
     * Constructeur
     * @param s Message
     */
    public ApiException(String s) {
        super(s);
    }
}
