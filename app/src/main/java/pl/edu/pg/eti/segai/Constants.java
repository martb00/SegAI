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
    public static final String DRIVE_ROOT = "1bZ9eCdeWUMCaoiwMvhbBVDXOJV5vdyHu";

    public static final String DRIVE_BIO_COLLECTING_MODE = "1DkvYfaYx1bgPPaljrhCTtX9hFb5RmajE";
    public static final String DRIVE_GLASS_COLLECTING_MODE = "1Dkxtew9YPytdf3-pAzjxFB-27WhZgZzJ";
    public static final String DRIVE_MIXED_COLLECTING_MODE = "1DqLHJYC-m9_M7OX-hc6EYttLMRohcnG6";
    public static final String DRIVE_PAPER_COLLECTING_MODE = "1DqaHR8aywQv4o9vjPIzvrNrkxlOgnLw3";
    public static final String DRIVE_PLASTIC_METAL_COLLECTING_MODE = "1DrfROZNvL1zu2ybvwD5DsX8UA51wNJQo";

    public static final String DRIVE_BIO_RESULT = "1RhikQtRTATalFzHJuhJXuEFIOXaAjKad";
    public static final String DRIVE_GLASS_RESULT = "1NT64QeVyJQ2b6yZCCZzMnvhpHLUAEk32";
    public static final String DRIVE_MIXED_RESULT = "1jPjRz8l_QZXgwkN2YFVs7k9eAFzPXOFV";
    public static final String DRIVE_PAPER_RESULT = "1RKW1c6QbZm_PhjP0m8fOWnFRFj9lXR-X";
    public static final String DRIVE_PLASTIC_METAL_RESULT = "1nhtAvHMWdANZ1exJw8X8V3Ge8X6ZJfV0";

    /** TRASH TYPES **/
    public static final String TRASH_TYPE_BIO = "Biodegradable";
    public static final String TRASH_TYPE_GLASS = "Glass";
    public static final String TRASH_TYPE_MIXED = "Mixed";
    public static final String TRASH_TYPE_PAPER = "Paper";
    public static final String TRASH_TYPE_PLASTIC_METAL = "Plastic and Metal";
}
