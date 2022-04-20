package pl.edu.pg.eti.segai;

public abstract class Constants {

    public static final String APPLICATION_NAME = "SegAI";

    /** CREDENTIALS FOR SERVICE ACCOUNT **/
    public static final String PATH_GOOGLE_DRIVE_CREDENTIALS = "/credentials.json";

    /** PHOTOS METADATA **/
    public static final String PATH_PHOTOS = "Pictures/MyImages/";
    public static final String EXTENSION_PHOTOS = ".jpg";
    public static final String MIME_TYPE_PHOTOS = "image/jpeg";

    /** GOOGLE DRIVE **/
    public static final String DRIVE_FOLDER_ROOT = "1bZ9eCdeWUMCaoiwMvhbBVDXOJV5vdyHu";
    public static final String DRIVE_FOLDER_BIO = "1DkvYfaYx1bgPPaljrhCTtX9hFb5RmajE";
    public static final String DRIVE_FOLDER_GLASS = "1Dkxtew9YPytdf3-pAzjxFB-27WhZgZzJ";
    public static final String DRIVE_FOLDER_MIXED = "1DqLHJYC-m9_M7OX-hc6EYttLMRohcnG6";
    public static final String DRIVE_FOLDER_PAPER = "1DqaHR8aywQv4o9vjPIzvrNrkxlOgnLw3";
    public static final String DRIVE_FOLDER_PLASTIC_METAL = "1DrfROZNvL1zu2ybvwD5DsX8UA51wNJQo";

    /** TRASH TYPES **/
    public static final String TRASH_TYPE_BIO = "Biodegradable";
    public static final String TRASH_TYPE_GLASS = "Glass";
    public static final String TRASH_TYPE_MIXED = "Mixed";
    public static final String TRASH_TYPE_PAPER = "Paper";
    public static final String TRASH_TYPE_PLASTIC_METAL = "Plastic and Metal";
}
