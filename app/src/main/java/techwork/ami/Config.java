package techwork.ami;

import android.text.InputType;

import jp.wasabeef.recyclerview.animators.ScaleInRightAnimator;

@SuppressWarnings("WeakerAccess")
public class Config {

	// Input type constant
	public static int inputNoVisiblePasswordType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
	public static int inputPromotionCodeType = InputType.TYPE_TEXT_VARIATION_PASSWORD;

	// Shared preferences keys
	public static final String KEY_SHARED_PREF ="cl.usm.techwork.ami";
	public static final String KEY_SP_ID="idPersona";
	public static final String KEY_SP_NAME="Persona.nombre";
	public static final String KEY_SP_LASTNAMES="Persona.apellidos";
	public static final String KEY_SP_EMAIL="Persona.email";
	public static final String KEY_SP_GENDER ="Persona.genero";
	public static final String KEY_SP_COMMUNE="Persona.comuna";
	public static final String KEY_SP_FIRST_LOGIN ="Persona.primerInicio";

	// Request URLs
	public static final String URL_GENERAL_SERVER="http://amiapp.cl";

	public static final String URL_GET_PROFILE="http://amiapp.cl/public/getData/Profile/getProfile.php";
	public static final String URL_CHANGE_ACCOUNT_STATUS ="http://amiapp.cl/public/getData/Profile/changeAccountStatus.php";
	public static final String URL_GET_MAP_OFFERS="http://amiapp.cl/public/getData/Maps/getData.php";
	public static final String URL_GET_AFTER_LOGIN_DATA="http://amiapp.cl/public/getData/AfterLogin/getOptions.php";
	public static final String URL_UPDATE_AFTER_LOGIN_DATA="http://amiapp.cl/public/getData/AfterLogin/updateAfterLogin.php";
	public static final String URL_UPDATE_PROFILE="http://amiapp.cl/public/getData/Profile/updateProfile.php";
	public static final String URL_LOGIN="http://amiapp.cl/public/getData/Login/login.php";
	public static final String URL_REGISTER="http://amiapp.cl/public/getData/Register/register.php";
	public static final String URL_RESTORE_PASS="http://amiapp.cl/public/getData/Login/findEmail.php";
	public static final String URL_GET_OFFERS="http://amiapp.cl/public/getData/Offers/getOffers.php";
	public static final String URL_OFFER_RESERVE="http://amiapp.cl/public/getData/Offers/reserveOffers.php";
	public static final String URL_OFFER_SAW = "http://amiapp.cl/public/getData/Offers/sawOffer.php";
	public static final String URL_GOD = "http://amiapp.cl/public/getData/Offers/offerDetails.php";
	public static final String URL_FILTER_OFFERS = "http://amiapp.cl/public/getData/Filter/filter.php";
    public static final String URL_GET_RESERVATIONS_OFFERS = "http://amiapp.cl/public/getData/ReservationsOffers/getReservationsOffers.php";
    public static final String URL_IMAGES_OFFER ="http://amiapp.cl/public/getData/Imagenes/";
	public static final String URL_NEW_NEED ="http://amiapp.cl/public/getData/Need/saveNeed.php";
	public static final String URL_NEED_DATA = "http://amiapp.cl/public/getData/Need/get_categories.php";
	public static final String URL_CONTACT_US = "http://amiapp.cl/public/getData/ContactUs/sendContactEmail.php";
	public static final String URL_DELETE_NEED = "http://amiapp.cl/public/getData/Need/ChangeStatusNeed.php";

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

	// GOD = Get Offer Details
	public static final String TAG_GOD_PRODUCT = "products";
	public static final String TAG_GOD_NAME = "nombre";
	public static final String TAG_GOD_DESCRIPTION = "descripcion";
	public static final String TAG_GOD_PRICE ="precio" ;
	public static final String TAG_GOD_IMAGE = "imagen";

	// FO = Filter Offers
	public static final String TAG_FO_ID_CATEGORY="idCategoria";
	public static final String TAG_FO_ID_STORE="idLocal";

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
	public static final String TAG_GRO_CALIFICATION = "calificacion";
	public static final String TAG_GRO_LOCCODE = "codigoLocal";

	//MRO = My Reservations Offers URLs
	public static final String URL_MRO_VALIDATE = "http://amiapp.cl/public/getData/ReservationsOffers/validateReservationOffer.php";
	public static final String URL_MRO_RATE = "http://amiapp.cl/public/getData/ReservationsOffers/rateOffer.php";

	// DO = Discard Offer URLs
	public static final String URL_DO_DISCARD = "http://amiapp.cl/public/getData/DiscardOffer/discardOffer.php";

    // Keys that will be used to send the request to php scripts
	public static final  String KEY_ID ="id"; // UP = Update Profile
	public static final  String KEY_NAME ="nombre";
	public static final  String KEY_LASTNAMES ="apellidos";
	public static final  String KEY_EMAIL ="email";
	public static final  String KEY_DATE ="fechaNacimiento";
	public static final  String KEY_PASS ="password";
	public static final  String KEY_PHONE ="telefono";
	public static final  String KEY_OCCUPATION ="idOcupacion";
	public static final  String KEY_COMMUNE ="idComuna";
	public static final  String KEY_GENDER ="idGenero";
	public static final  String KEY_STATUS ="estado";

	// Need Keys
	public static final  String KEY_NE_TITLE="Title";
	public static final  String KEY_NE_DESCRIPTION="Description";
	public static final  String KEY_NE_MONEY="Money";
	public static final  String KEY_NE_DAYS="NeedDays";
	public static final  String KEY_NE_LAT="Lat";
	public static final  String KEY_NE_LON="Lon";
	public static final  String KEY_NE_USER_ID="User_id";
	public static final  String KEY_NE_SUBCATEGORY_ID="Subcategory_id";
	public static final  String KEY_NE_COMMUNE_ID="Commune_id";
	public static final  String KEY_NE_LOCATION="userlocation";
	public static final  String KEY_NE_ID="NeedId";

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
	public static final String TAG_ID_STORE ="idLocal";
	public static final String TAG_ID_OCCUPATION ="idOcupacion";
	public static final String TAG_ID_GENDER ="idGenero";
	public static final String TAG_ID_COUNTRY ="idPais";
	public static final String TAG_ID_REGION ="idRegion";
	public static final String TAG_ID_PROVINCE ="idProvincia";
	public static final String TAG_ID_COMMUNE ="idComuna";
	public static final String TAG_ID_CATEGORIES ="idCategoria";
	public static final String TAG_ID_SUBCATEGORIES ="idSubcategoria";
	public static final String TAG_OCCUPATION_NAME ="nombre";
	public static final String TAG_PROFILE ="profile";
	public static final String TAG_OCCUPATIONS ="occupations";
	public static final String TAG_COUNTRIES ="countries";
	public static final String TAG_GENDERS ="genders";
	public static final String TAG_NAME ="nombre";
	public static final String TAG_LASTNAMES ="apellidos";
	public static final String TAG_EMAIL ="email";
	public static final String TAG_DATE ="fechaNacimiento";
	public static final String TAG_IMAGE ="imagen";
	public static final String TAG_PHONE ="telefono";
	public static final String TAG_OCCUPATION ="ocupacion";
	public static final String TAG_GENDER ="genero";
	public static final String TAG_POINTS ="puntos";
	public static final String TAG_EXIST_EMAIL ="emailExiste";

	//Tags and Url for CategoryFragments
	public static final String URL_GET_CATEGORY="http://amiapp.cl/public/getData/CategoryList/getCategoryList.php";
	public static final String URL_IMAGES_CATEGORY="http://amiapp.cl/public/getData/Imagenes/";
	public static final String TAG_GC_CATEGORY= "categorias";
	public static final String TAG_GC_ID="idCategoria";
	public static final String TAG_GC_NAME="nombre";
	public static final String TAG_GC_IMAGE ="imagen";

	//Tags and keys for NeedFragment
	public static final String URL_GET_NEED="http://amiapp.cl/public/getData/Need/getNeed.php";
	public static final String KEY_GN_IDPERSON="idPersona";
	public static final String TAG_GN_NEED= "needs";
	public static final String TAG_GN_IDNEED ="idNecesidad";
	public static final String TAG_GN_TITTLE ="titulo";
	public static final String TAG_GN_DESCRIPTION="descripcion";
	public static final String TAG_GN_EXPIRATIONDATE = "fechaExpiracion";
	public static final String TAG_GN_PRICEMAX="precioMax";
	public static final String TAG_GN_LATITUDE = "latitud";
	public static final String TAG_GN_LONGITUDE="longitud";
	public static final String TAG_GN_RADIO="radio";
	public static final String TAG_GN_OFFERS_COMPANY = "ofertasEmpresas";
	public static final String TAG_GN_NDISCARD_OFFERS="nDescartadas";

	//Tags and keys for NeedOfferActivity
	public static final String URL_GET_NEED_OFFER="http://amiapp.cl/public/getData/Need/getNeedOffer.php";
	public static final String KEY_GNO_IDNEED="idNecesidad";
	public static final String TAG_GNO_NEED="needOffers";
	public static final String TAG_GNO_IDNEED="idNecesidad";
	public static final String TAG_GNO_IDLOCAL="idLocal";
	public static final String TAG_GNO_IDOFFER="idOferta";
	public static final String TAG_GNO_TITTLE="titulo";
	public static final String TAG_GNO_DESCRIPTION="descripcion";
	public static final String TAG_GNO_STOCK ="stock";
	public static final String TAG_GNO_CODPROMOTION="codPromocion";
	public static final String TAG_GNO_DATEINI="fechaInicio";
	public static final String TAG_GNO_DATEFIN="fechaTermino";
	public static final String TAG_GNO_DATETIMEFIN="fechaTermino";
	public static final String TAG_GNO_PRICEOFFER="precioOferta";
	public static final String TAG_GNO_MAXPPERSON="maxPPersona";
	public static final String TAG_GNO_COMPANY="nombreEmpresa";

	//Tags and Keys for NeedreservationsActivity
	public static final String URL_GET_NEED_RESERVATIONS="http://amiapp.cl/public/getData/Need/getNeedReservations.php";
	public static final String KEY_GNR_IDPERSON="idPersona";
	public static final String TAG_GNR_NEED="needReservations";
	public static final String TAG_GNR_IDNEED="idNecesidad";
	public static final String TAG_GNR_IDLOCAL="idLocal";
	public static final String TAG_GNR_IDOFFER="idOferta";
	public static final String TAG_GNR_TITTLE="titulo";
	public static final String TAG_GNR_DESCRIPTION="descripcion";
	public static final String TAG_GNR_STOCK ="stock";
	public static final String TAG_GNR_CODPROMOTION="codPromocion";
	public static final String TAG_GNR_QUANTITY="cantidad";
	public static final String TAG_GNR_CASHED="cobrado";
	public static final String TAG_GNR_CALIFICATION="calificacion";
	public static final String TAG_GNR_DATERESERV="fechaReserva";
	public static final String TAG_GNR_DATECASHED="fechaCobro";
	public static final String TAG_GNR_DATEINI="fechaInicio";
	public static final String TAG_GNR_DATEFIN="fechaTermino";
	public static final String TAG_GNR_PRICEOFFER="precioOferta";


	//Discard NeedOfferActivity
	public static final String URL_DISCARD_NEED_OFFER="http://amiapp.cl/public/getData/Need/discardNeedOffer.php";
	public static final String KEY_DNO_IDOFFER="idOferta";
	public static final String KEY_DNO_IDPERSON="idPersona";
	//Accept NeedOfferActivity
	public static final String URL_ACCEPT_NEED_OFFER="http://amiapp.cl/public/getData/Need/acceptNeedOffer.php";
	public static final String KEY_ANO_IDOFFER="idOferta";
	public static final String KEY_ANO_IDPERSON="idPersona";
	public static final String KEY_ANO_MAXPPERSON="cantidad";
	//Get Products for NeedOfferViewActivity
	public static final String URL_GET_PRODUCT_OFFER="http://amiapp.cl/public/getData/Need/getProductNeedOffer.php";
	public static final String KEY_PNO_IDOFFER="idOferta";
	public static final String TAG_PNO_PRODUCT="products";
	public static final String TAG_PNO_NAME="nombre";

	//Get Local details of NeedOffer
	public static final String URL_GET_LOCAL="http://amiapp.cl/public/getData/Need/getLocal.php";
	public static final String KEY_GL_IDLOCAL="idLocal";
	public static final String TAG_GL_LOCAL="local";
	public static final String TAG_GL_LAT="latitud";
	public static final String TAG_GL_LONG="longitud";
	public static final String TAG_GL_ADDRESS="direccion";
	public static final String TAG_GL_WEB="pagina";
	public static final String TAG_GL_IMAGE="imagen";
	public static final String TAG_GL_COMMUNE="comuna";

	// Contact Us Keys
	public static final String TAG_CU_NAME = "nombre";
	public static final String TAG_CU_MAIL = "email";
	public static final String TAG_CU_SUBJECT = "asunto";
	public static final String TAG_CU_MESSAGE = "mensaje";

	// Money format
	public static final String CLP_FORMAT = "%,d";

	// My Reservations offers keys
	public static final String KEY_RESERVE_OFFER_ID = "idOferta";
	public static final String KEY_RESERVE_PERSON_ID = "idPersona";
	public static final String KEY_RESERVE_QUANTITY = "cantidad";
	public static final String KEY_RESERVE_RESERVE_DATE = "fechaReserva";
	public static final String KEY_RESERVE_RATE = "calificacion";

	// Discard offer keys
	public static final String KEY_DO_OFFER_ID = "idOferta";
	public static final String KEY_DO_PERSON_ID = "idPersona";

	// Offer Detail keys
	public static final String KEY_OD_OFFER_ID = "idOferta";

	// Date format
	public static final String DATETIME_FORMAT_DB="yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT="%02d/%02d/%04d";
	public static final String DATETIME_FORMAT="%02d/%02d/%04d %02d:%02d:%02d";
	public static final String DATETIME_FORMAT_ANDROID="dd/MM/yyyy HH:mm:ss";


	//Tags used in the JSON String Need
	public static final String TAG_ID = "idCategoria";

	//JSON array name
	public static final String JSON_ARRAY = "result";

    //Keys for Fragments (MainPageAdapter)
    public static final int HOME = 0;
    public static final int CATEGORY = 1;
    public static final int NEED = 2;
    public static final int PAGE_COUNT = 3;
}
