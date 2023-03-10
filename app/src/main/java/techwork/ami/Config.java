package techwork.ami;

import android.text.InputType;

import jp.wasabeef.recyclerview.animators.ScaleInRightAnimator;

@SuppressWarnings("WeakerAccess")
public class Config {

	public static final double MILIS_TO_MIN = 1.0 / (1000 * 60);
	// Slack time (in minutes, change only first number) that define if the notification is shown or not (if greater than this, no show).
	public static final double NOTIFICATION_SLACK_TIME= 5.0;

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
	public static final String PARAMS_URL_GET_PROFILE = "type=%s&id=%s";
	public static final String URL_CHANGE_ACCOUNT_STATUS ="http://amiapp.cl/public/getData/Profile/changeAccountStatus.php";
	public static final String URL_GET_MAP_OFFERS="http://amiapp.cl/public/getData/Maps/getData.php";
	public static final String URL_GET_AFTER_LOGIN_DATA="http://amiapp.cl/public/getData/AfterLogin/getOptions.php";
	public static final String URL_UPDATE_AFTER_LOGIN_DATA="http://amiapp.cl/public/getData/AfterLogin/updateAfterLogin.php";
	public static final String URL_UPDATE_PROFILE="http://amiapp.cl/public/getData/Profile/updateProfile.php";
	public static final String URL_LOGIN="http://amiapp.cl/public/getData/Login/login.php";
	public static final String URL_HASH="http://amiapp.cl/public/getData/Login/getHash.php";
	public static final String URL_REGISTER="http://amiapp.cl/public/getData/Register/register.php";
	public static final String URL_REGISTER_EMAIL = "http://amiapp.cl/public/getData/Register/sendWelcomeEmail.php";
	public static final String URL_RESTORE_PASS="http://amiapp.cl/public/getData/Login/findEmail.php";
	public static final String URL_GET_OFFERS="http://amiapp.cl/public/getData/Offers/getOffers.php";
	public static final String URL_OFFER_RESERVE="http://amiapp.cl/public/getData/Offers/reserveOffers.php";
	public static final String URL_OFFER_SAW = "http://amiapp.cl/public/getData/Offers/sawOffer.php";
	public static final String URL_GOD = "http://amiapp.cl/public/getData/Offers/offerDetails.php";
	public static final String URL_FILTER_OFFERS = "http://amiapp.cl/public/getData/Filter/filter.php";
	public static final String URL_GET_RESERVATIONS_OFFERS = "http://amiapp.cl/public/getData/ReservationsOffers/getReservationsOffers.php";
	public static final String URL_IMAGES_OFFER ="http://amiapp.cl/encargado/uploads/";
	public static final String URL_NEW_NEED ="http://amiapp.cl/public/getData/Need/saveNeed.php";
	public static final String URL_NEED_DATA = "http://amiapp.cl/public/getData/Need/get_categories.php";
	public static final String URL_CONTACT_US = "http://amiapp.cl/public/getData/ContactUs/sendContactEmail.php";
	public static final String URL_DELETE_NEED = "http://amiapp.cl/public/getData/Need/discardOrder.php";

	// GO = Get Offers
	//TODO: Cambiar los nombres a la actual definicion.
	public static final String TAG_GO_OFFERS="offers";
	public static final String TAG_GO_OFFER_ID="id";
	public static final String TAG_GO_IDLOCAL="idLocal";
	public static final String TAG_GO_TITLE="titulo";
	public static final String TAG_GO_DESCRIPTION="descripcion";
	public static final String TAG_GO_STOCK="stock";
	public static final String TAG_GO_PROMCOD="codPromocion";
	public static final String TAG_GO_DATEINI="fechaInicio";
	public static final String TAG_GO_DATEFIN="fechaTermino";
	public static final String TAG_GO_DATETIMEFIN="fechaTermino";
	public static final String TAG_GO_STATE="estado";
	public static final String TAG_GO_PRICE="precio";
	public static final String TAG_GO_TOTALPRICE="precioTotal";
	public static final String TAG_GO_MAXXPER="maxPPersona";
	public static final String TAG_GO_COMPANY="empresa";
	public static final String TAG_GO_IMAGE ="imagen" ;
	public static final String TAG_GO_NO_RESERVE_OPTION = "mostrarOpcionReserva";

	// GOD = Get Promotion Details
	//TODO: Cambiar los nombres a la actual definicion.
	public static final String TAG_GOD_PRODUCT = "products";
	public static final String TAG_GOD_NAME = "nombre";
	public static final String TAG_GOD_DESCRIPTION = "descripcion";
	public static final String TAG_GOD_PRICE ="precio" ;
	public static final String TAG_GOD_IMAGE = "imagen";
	public static final String TAG_GOD_QUANTITY = "cantidad";

	// FO = Filter Offers
	//TODO: Cambiar los nombres a la actual definicion.
	public static final String TAG_FO_ID_CATEGORY="idCategoria";
	public static final String TAG_FO_ID_STORE="idLocal";

	//GRO = Get Reservations Offers (not same)
	//TODO: Cambiar los nombres a la actual definicion.
	public static final String TAG_GRO = "reservationsOffers";
	public static final String TAG_GRO_ID_OFFER = "idOferta";
	public static final String TAG_GRO_IDLOCAL="idLocal";
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
	public static final String TAG_GRO_CHARGED="cobrado";
	public static final String TAG_GRO_MAXXPER = "maxPPersona";
	public static final String TAG_GRO_STOCK = "stock";
	public static final String TAG_GRO_TOTALPRICE = "precioTotal";

	//URL MapsActivity
	public static final String URL_ENTERPRISE_IMAGE="http://amiapp.cl/admin/uploads/";

	//MRO = My Reservations Offers URLs
	//TODO: Cambiar los nombres a la actual definicion.
	public static final String URL_MRO_VALIDATE = "http://amiapp.cl/public/getData/ReservationsOffers/validateReservationOffer.php";
	public static final String URL_MRO_RATE = "http://amiapp.cl/public/getData/ReservationsOffers/rateOffer.php";
	public static final String URL_MRO_DELETE = "http://amiapp.cl/public/getData/ReservationsOffers/deleteOffer.php";

	// DO = Discard Promotion URLs
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
	//TODO: Cambiar los nombres a la actual definicion.
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

	//Hash keys and tags
	public static final String URL_REGISTER_HASH="http://amiapp.cl/public/getData/Register/registerHash.php";
	public static final String URL_LOGIN_HASH="http://amiapp.cl/public/getData/Login/loginHash.php";
	public static final String KEY_HASH_EMAIL="email";
	public static final String KEY_HASH_PASSWORD="passwordHash";
	public static final String TAG_GET_HASH="hash";
	public static final String TAG_GET_PASSWORD_HASH="passwordHash";

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

	//OrderFragment
	public static final String URL_GET_ORDER="http://amiapp.cl/public/getData/Need/getOrders.php";
	public static final String KEY_GET_ORDER_IDPERSON="idPersona";
	public static final String TAG_GET_ORDER= "pedidos";
	public static final String TAG_GET_ORDER_IDNEED ="idNecesidad";
	public static final String TAG_GET_ORDER_TITTLE ="titulo";
	public static final String TAG_GET_ORDER_DESCRIPTION="descripcion";
	public static final String TAG_GET_ORDER_EXPIRATIONDATE = "fechaExpiracion";
	public static final String TAG_GET_ORDER_PRICEMAX="precioMax";
	public static final String TAG_GET_ORDER_LATITUDE = "latitud";
	public static final String TAG_GET_ORDER_LONGITUDE="longitud";
	public static final String TAG_GET_ORDER_RADIO="radio";
	public static final String TAG_GET_ORDER_OFFERS_COMPANY = "ofertasEmpresas";
	public static final String TAG_GET_ORDER_NDISCARD_OFFERS="nDescartadas";
	public static final String TAG_GET_ORDER_SUBCATEGORY="subCategoria";

	//OfferActivity (NeedOffer after)
	public static final String URL_GET_OFFER="http://amiapp.cl/public/getData/Need/getOffers.php";
	public static final String URL_IMAGES_OFFER_2="http://amiapp.cl/encargado/uploads/";
	public static final String KEY_GET_OFFER_IDNEED="idNecesidad";
	public static final String TAG_GET_OFFER_NEED="ofertas";
	public static final String TAG_GET_OFFER_IDNEED="idNecesidad";
	public static final String TAG_GET_OFFER_IDLOCAL="idLocal";
	public static final String TAG_GET_OFFER_IDOFFER="idOferta";
	public static final String TAG_GET_OFFER_TITTLE="titulo";
	public static final String TAG_GET_OFFER_DESCRIPTION="descripcion";
	public static final String TAG_GET_OFFER_STOCK ="stock";
	public static final String TAG_GET_OFFER_CODPROMOTION="codPromocion";
	public static final String TAG_GET_OFFER_DATEINI="fechaInicio";
	public static final String TAG_GET_OFFER_DATEFIN="fechaTermino";
	public static final String TAG_GET_OFFER_DATETIME_FIN="fechaTermino";
	public static final String TAG_GET_OFFER_PRICEOFFER="precioOferta";
	public static final String TAG_GET_OFFER_PRICE_TOTAL="precioTotal";
	public static final String TAG_GET_OFFER_MAXPPERSON="maxPPersona";
	public static final String TAG_GET_OFFER_COMPANY="nombreEmpresa";
	public static final String TAG_GET_OFFER_IMAGE="imagen";

	//Get Products for OffersViewActivity
	public static final String URL_GET_PRODUCT_OFFER="http://amiapp.cl/public/getData/Need/getProductOffer.php";
	public static final String URL_IMAGES_PRODUCTS="http://amiapp.cl/encargado/uploads/";
	public static final String KEY_GET_PRODUCT_OFFER_IDOFFER="idOferta";
	public static final String TAG_GET_PRODUCT_OFFER="productos";
	public static final String TAG_GET_PRODUCT_OFFER_NAME="nombre";
	public static final String TAG_GET_PRODUCT_OFFER_DESCRIPTION="descripcion";
	public static final String TAG_GET_PRODUCT_OFFER_PRICE="precio";
	public static final String TAG_GET_PRODUCT_OFFER_IMAGE="imagen";
	public static final String TAG_GET_PRODUCT_OFFER_QUANTITY="cantidad";
	//Discard OffersActivity
	public static final String URL_DISCARD_OFFER="http://amiapp.cl/public/getData/Need/discardOffer.php";
	public static final String KEY_DISCARD_OFFER_IDOFFER="idOferta";
	public static final String KEY_DISCARD_OFFER_IDPERSON="idPersona";
	//Accept OffersActivity
	public static final String URL_ACCEPT_OFFER="http://amiapp.cl/public/getData/Need/acceptOffer.php";
	public static final String KEY_ACCEPT_OFFER_IDOFFER="idOferta";
	public static final String KEY_ACCEPT_OFFER_IDPERSON="idPersona";
	public static final String KEY_ACCEPT_OFFER_MAXPPERSON="cantidad";

	//Local details of OfferView
	public static final String URL_GET_LOCAL="http://amiapp.cl/public/getData/Need/getLocal.php";
	public static final String URL_LOCAL_IMAGE="http://amiapp.cl/admin/uploads/";
	public static final String KEY_GET_LOCAL_IDLOCAL="idLocal";
	public static final String TAG_GET_LOCAL="local";
	public static final String TAG_GET_LOCAL_LAT="latitud";
	public static final String TAG_GET_LOCAL_LONG="longitud";
	public static final String TAG_GET_LOCAL_ADDRESS="direccion";
	public static final String TAG_GET_LOCAL_WEB="pagina";
	public static final String TAG_GET_LOCAL_IMAGE="imagen";
	public static final String TAG_GET_LOCAL_COMMUNE="comuna";
	public static final String TAG_LOCAL_ACTIVITY_COMPANY = "nameCompany";
	
	//Offers Reservations
	public static final String URL_GET_OFFER_RESERVATIONS="http://amiapp.cl/public/getData/NeedReserved/getOfferReserved.php";
	public static final String URL_VALIDATE_OFFER_RESERV ="http://amiapp.cl/public/getData/NeedReserved/validateOfferReserved.php";
	public static final String URL_OFFER_RATE ="http://amiapp.cl/public/getData/NeedReserved/rateOfferReserved.php";
	public static final String KEY_GET_OFFER_RESERVED_IDPERSON="idPersona";
	public static final String KEY_GET_OFFER_RESERVED_IDOFFER = "idOferta";
	public static final String KEY_GET_OFFER_RESERVED_QUANTITY = "cantidad";
	public static final String KEY_GET_OFFER_RESERVED_DATE_RESERV = "fechaReserva";
	public static final String KEY_GET_OFFER_RESERVED_RATE = "calificacion";
	public static final String TAG_GET_OFFER_RESERVED="ofertasReservadas";
	public static final String TAG_GET_OFFER_RESERVED_IDNEED="idNecesidad";
	public static final String TAG_GET_OFFER_RESERVED_IDLOCAL="idLocal";
	public static final String TAG_GET_OFFER_RESERVED_IDOFFER="idOferta";
	public static final String TAG_GET_OFFER_RESERVED_TITTLE="titulo";
	public static final String TAG_GET_OFFER_RESERVED_DESCRIPTION="descripcion";
	public static final String TAG_GET_OFFER_RESERVED_STOCK ="stock";
	public static final String TAG_GET_OFFER_RESERVED_CODPROMOTION="codPromocion";
	public static final String TAG_GET_OFFER_RESERVED_QUANTITY="cantidad";
	public static final String TAG_GET_OFFER_RESERVED_CASHED="cobrado";
	public static final String TAG_GET_OFFER_RESERVED_CALIFICATION="calificacion";
	public static final String TAG_GET_OFFER_RESERVED_DATERESERV="fechaReserva";
	public static final String TAG_GET_OFFER_RESERVED_DATECASHED="fechaCobro";
	public static final String TAG_GET_OFFER_RESERVED_DATEINI="fechaInicio";
	public static final String TAG_GET_OFFER_RESERVED_DATEFIN="fechaTermino";
	public static final String TAG_GET_OFFER_RESERVED_DATETIME="fechaTermino";
	public static final String TAG_GET_OFFER_RESERVED_PRICEOFFER="precioOferta";
	public static final String TAG_GET_OFFER_RESERVED_PRICE_TOTAL="precioTotal";
	public static final String TAG_GET_OFFER_RESERVED_COMPANY="empresa";
	public static final String TAG_GET_OFFER_RESERVED_LOCALCODE="codigoLocal";
	public static final String TAG_GET_OFFER_RESERVED_IMAGE="imagen";

	//Delete offer reservations
	public static final String URL_DELETE_OFFER_RESERVED="http://amiapp.cl/public/getData/NeedReserved/deleteOfferReserved.php";
	public static final String KEY_DELETE_OFFER_RESERVED_IDOFFER="idOferta";
	public static final String KEY_DELETE_OFFER_RESERVED_IDPERSON="idPersona";
	public static final String KEY_DELETE_OFFER_RESERVED_QUANTITY="cantidad";

	// Contact Us Keys
	public static final String TAG_CU_NAME = "nombre";
	public static final String TAG_CU_MAIL = "email";
	public static final String TAG_CU_SUBJECT = "asunto";
	public static final String TAG_CU_MESSAGE = "mensaje";

	// Money format
	public static final String CLP_FORMAT = "$ %,d";

	// Quantity and price format
	public static final String QUANTITYPRICE_FORMAT = "%s x %s %s";

	// My Reservations offers keys
	public static final String KEY_RESERVE_OFFER_ID = "idOferta";
	public static final String KEY_RESERVE_PERSON_ID = "idPersona";
	public static final String KEY_RESERVE_QUANTITY = "cantidad";
	public static final String KEY_RESERVE_RESERVE_DATE = "fechaReserva";
	public static final String KEY_RESERVE_RATE = "calificacion";

	// Discard offer keys
	public static final String KEY_DO_OFFER_ID = "idOferta";
	public static final String KEY_DO_PERSON_ID = "idPersona";

	// Promotion Detail keys
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
