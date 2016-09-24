package techwork.ami;

import android.text.InputType;

public class Config {
	// Input type constant
	public static int inputPasswordType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;

	// Shared preferences keys
	public static final String KEY_SHARED_PREF ="cl.usm.techwork.ami";
	public static final String KEY_SP_ID="idPersona";
	public static final String KEY_SP_NAME="Persona.nombre";
	public static final String KEY_SP_EMAIL="Persona.email";
	public static final String KEY_SP_GENRE="Persona.genero";

	// Request URLs
	public static final String URL_GET_PROFILE="http://amiapp.cl/public/getData/Profile/getProfile.php?id=";
	public static final String URL_GET_MAP_OFFERS="http://amiapp.cl/public/getData/Maps/getData.php";
	public static final String URL_GET_AFTER_LOGIN_DATA="http://amiapp.cl/public/getData/AfterLogin/getOptions.php?";
	public static final String URL_GET_CATEGORY="http://amiapp.cl/public/getData/CategoryList/getCategoryList.php";
	public static final String URL_UPDATE_AFTER_LOGIN_DATA="http://amiapp.cl/public/getData/AfterLogin/updateAfterLogin.php";
	public static final String URL_UPDATE_PROFILE="http://amiapp.cl/public/getData/Profile/updateProfile.php";
	public static final String URL_LOGIN="http://amiapp.cl/public/getData/Login/login.php";
	public static final String URL_REGISTER="http://amiapp.cl/public/getData/Register/register.php";
	public static final String URL_RESTORE_PASS="http://amiapp.cl/public/getData/Login/findEmail.php?email=";
	public static final String URL_GET_OFFERS="http://amiapp.cl/public/getData/Offers/getOffers2.php";
	public static final String URL_OFFER_RESERVE="http://amiapp.cl/public/getData/Offers/reserveOffers.php";
	public static final String URL_GET_NEED="http://amiapp.cl/public/getData/Need/getNeed.php";
    public static final String URL_GET_RESERVATIONS_OFFERS = "http://amiapp.cl/public/getData/ReservationsOffers/getReservationsOffers.php";
    public static final String URL_IMAGES_OFFER ="http://amiapp.cl/public/getData/Imagenes/";

    // GO = Get Offers
	public static final String TAG_GO_OFFERS="offers";
	public static final String TAG_GO_OFFER_ID="id";
	public static final String TAG_GO_TITLE="titulo";
	public static final String TAG_GO_DESCRIPTION="descripcion";
	public static final String TAG_GO_STOCK="stock";
	public static final String TAG_GO_PROMCOD="codPromocion";
	public static final String TAG_GO_DATEINI="fechaInicio";
	public static final String TAG_GO_DATEFIN="fechaTermino";
	public static final String TAG_GO_STATE="estado";
	public static final String TAG_GO_PRICE="precio";
	public static final String TAG_GO_MAXXPER="maxPPersona";
	public static final String TAG_GO_COMPANY="empresa";
	public static final String TAG_GO_IMAGE ="imagen" ;

    //GRO = Get Reservations Offers (not same)
    public static final String TAG_GRO = "reservationsOffers";
    public static final String TAG_GRO_ID_OFFER = "idOferta";
    public static final String TAG_GRO_TITLE = "titulo";
    public static final String TAG_GRO_DESCRIPTION = "descripcion";
    public static final String TAG_GRO_PROMOCOD = "codPromocion";
    public static final String TAG_GRO_DATEINI = "fechaInicio";
    public static final String TAG_GRO_DATEFIN = "fechaTermino";
    public static final String TAG_GRO_PRICE = "precio";
    public static final String TAG_GRO_STATE = "estado";
    public static final String TAG_GRO_IMAGE = "imagen";
    public static final String TAG_GRO_COMPANY = "empresa";
    public static final String TAG_GRO_QUANTITY = "cantidad";
    public static final String TAG_GRO_RESERDATE = "fechaReserva";
    public static final String TAG_GRO_PAYDATE = "fechaCobro";

	//MRO = My Reservations Offers URLs
	public static final String URL_MRO_VALIDATE = "http://amiapp.cl/public/getData/ReservationsOffers/validateReservationOffer.php?";

    // Keys that will be used to send the request to php scripts
	public static final  String KEY_ID ="id"; // UP = Update Profile
	public static final  String KEY_NAME ="nombre";
	public static final  String KEY_EMAIL ="email";
	public static final  String KEY_DATE ="fechaNacimiento";
	public static final  String KEY_PASS ="password";
	public static final  String KEY_PHONE ="telefono";
	public static final  String KEY_OCCUPATION ="idOcupacion";
	public static final  String KEY_COMMUNE ="idComuna";
	public static final  String KEY_GENRE ="idGenero";

	// login Keys
	public static final  String KEY_LI_PASS="password";
	public static final  String KEY_LI_EMAIL="email";

	// JSON tags
	public static final String TAG_LATITUDE ="latitud";
	public static final String TAG_LONGITUDE ="longitud";
	public static final String TAG_OFFERS_QUANTITY ="cantidadOfertas";
	public static final String TAG_ADDRESS ="direccion";
	public static final String TAG_ID_P ="idPersona";
	public static final String TAG_FIRST_LOGIN ="primerInicio";
	public static final String TAG_RESULT ="result";
	public static final String TAG_TYPE ="type";
	public static final String TAG_ID_OCCUPATION ="idOcupacion";
	public static final String TAG_ID_GENRE ="idGenero";
	public static final String TAG_ID_COUNTRY ="idPais";
	public static final String TAG_ID_REGION ="idRegion";
	public static final String TAG_ID_PROVINCE ="idProvincia";
	public static final String TAG_ID_COMMUNE ="idComuna";
	public static final String TAG_OCCUPATION_NAME ="nombre";
	public static final String TAG_PROFILE ="profile";
	public static final String TAG_OCCUPATIONS ="occupations";
	public static final String TAG_COUNTRIES ="countries";
	public static final String TAG_GENRES ="genres";
	public static final String TAG_NAME ="nombre";
	public static final String TAG_EMAIL ="email";
	public static final String TAG_DATE ="fechaNacimiento";
	public static final String TAG_PHONE ="telefono";
	public static final String TAG_OCCUPATION ="ocupacion";
	public static final String TAG_GENRE ="genero";
	public static final String TAG_EXIST_EMAIL ="emailExiste";


	//Tags and Url for CategoryFragments
	public static final String URL_IMAGES_CATEGORY="http://amiapp.cl/public/getData/Imagenes/";
	public static final String TAG_GC_CATEGORY= "categorias";
	public static final String TAG_GC_ID="idCategoria";
	public static final String TAG_GC_NAME="nombre";
	public static final String TAG_GC_IMAGE ="imagen";

	//Tags and keys for NeedFragment
	public static final String KEY_GN_ID="id";
	public static final String TAG_GN_NEED= "needs";
	public static final String TAG_GN_IDNEED ="idNecesidad";
	public static final String TAG_GN_IDPER="idPersona";
	public static final String TAG_GN_TITTLE ="titulo";
	public static final String TAG_GN_DESCRIPTION="descripcion";
	public static final String TAG_GN_EXPIRATIONDATE = "fechaExpiracion";
	public static final String TAG_GN_PRICEMIN="precioMin";
	public static final String TAG_GN_LATITUDE = "latitud";
	public static final String TAG_GN_LONGITUDE="longitud";
	public static final String TAG_GN_RADIO="radio";
	public static final String TAG_GN_OFFERS_COMPANY = "ofertasEmpresas";

	// Money format
	public static final String CLP_FORMAT = "%,d";

	// Reserve offers keys
	public static final String KEY_RESERVE_OFFER_ID = "idOferta";
	public static final String KEY_RESERVE_PERSON_ID = "idPersona";
	public static final String KEY_RESERVE_QUANTITY = "cantidad";
	public static final String KEY_RESERVE_RESERVE_DATE = "fechaReserva";

	// Date format
	public static final String DATETIME_FORMAT_DB="yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT="%02d/%02d/%04d";

    //Keys for Fragments (MainPageAdapter)
    public static final int HOME = 0;
    public static final int CATEGORY = 1;
    public static final int NECESIDAD = 2;
    public static final int PAGE_COUNT = 3;

}
