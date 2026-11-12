package com.example.util

import java.util.Calendar

data class StateGeoInfo(
    val stateName: String,
    val districts: List<String>,
    val regionalLanguages: List<AppLanguage>,
    val primaryCrops: List<String>
)

enum class AgroSeason(val code: String, val englishName: String, val gujaratiName: String, val hindiName: String, val months: String) {
    KHARIF("kharif", "Kharif Season (Monsoon)", "ખરીફ ઋતુ (ચોમાસુ)", "खरीफ मौसम (मानसून)", "June to October"),
    RABI("rabi", "Rabi Season (Winter)", "રવી ઋતુ (શિયાળુ)", "रबी मौसम (सर्दियां)", "October to April"),
    ZAID("zaid", "Zaid Season (Summer)", "જાયદ ઋતુ (ઉનાળુ)", "जायद मौसम (गर्मी)", "March to June");

    companion object {
        fun getCurrent(): AgroSeason {
            val month = Calendar.getInstance().get(Calendar.MONTH) // 0-based: 0=Jan, 11=Dec
            return when (month) {
                Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER -> KHARIF
                Calendar.NOVEMBER, Calendar.DECEMBER, Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH -> RABI
                Calendar.APRIL, Calendar.MAY -> ZAID
                else -> RABI
            }
        }
    }
}

object IndiaGeoData {

    const val DEFAULT_COUNTRY = "India"
    const val DEFAULT_STATE = "Gujarat"
    const val DEFAULT_DISTRICT = "Rajkot"

    val gujaratDistricts = listOf(
        "Ahmedabad", "Amreli", "Anand", "Aravalli", "Banaskantha", "Bharuch",
        "Bhavnagar", "Botad", "Chhota Udaipur", "Dahod", "Dang", "Devbhoomi Dwarka",
        "Gandhinagar", "Gir Somnath", "Jamnagar", "Junagadh", "Kheda", "Kutch",
        "Mahisagar", "Mehsana", "Morbi", "Narmada", "Navsari", "Panchmahal",
        "Patan", "Porbandar", "Rajkot", "Sabarkantha", "Surat", "Surendranagar",
        "Tapi", "Vadodara", "Valsad"
    )

    val statesList: List<StateGeoInfo> = listOf(
        StateGeoInfo(
            stateName = "Gujarat",
            districts = gujaratDistricts,
            regionalLanguages = listOf(AppLanguage.GUJARATI, AppLanguage.HINDI, AppLanguage.ENGLISH),
            primaryCrops = listOf("Cotton (કપાસ)", "Groundnut (મગફળી)", "Wheat (ઘઉં)", "Castor (દિવેલા)", "Cumin (જીરું)", "Sesame (તલ)")
        ),
        StateGeoInfo(
            stateName = "Maharashtra",
            districts = listOf(
                "Ahmednagar", "Akola", "Amravati", "Aurangabad", "Beed", "Bhandara", "Buldhana",
                "Chandrapur", "Dhule", "Gadchiroli", "Gondia", "Hingoli", "Jalgaon", "Jalna",
                "Kolhapur", "Latur", "Mumbai Suburban", "Nagpur", "Nanded", "Nandurbar", "Nashik",
                "Osmanabad", "Palghar", "Parbhani", "Pune", "Raigad", "Ratnagiri", "Sangli",
                "Satara", "Sindhudurg", "Solapur", "Thane", "Wardha", "Washim", "Yavatmal"
            ),
            regionalLanguages = listOf(AppLanguage.MARATHI, AppLanguage.HINDI, AppLanguage.ENGLISH),
            primaryCrops = listOf("Cotton", "Sugarcane", "Soybean", "Pomegranate", "Onion", "Turmeric")
        ),
        StateGeoInfo(
            stateName = "Punjab",
            districts = listOf(
                "Amritsar", "Barnala", "Bathinda", "Faridkot", "Fatehgarh Sahib", "Fazilka",
                "Firozpur", "Gurdaspur", "Hoshiarpur", "Jalandhar", "Kapurthala", "Ludhiana",
                "Mansa", "Moga", "Muktsar", "Pathankot", "Patiala", "Rupnagar", "Sangrur", "Tarn Taran"
            ),
            regionalLanguages = listOf(AppLanguage.PUNJABI, AppLanguage.HINDI, AppLanguage.ENGLISH),
            primaryCrops = listOf("Wheat", "Rice", "Cotton", "Maize", "Mustard", "Sugarcane")
        ),
        StateGeoInfo(
            stateName = "Haryana",
            districts = listOf(
                "Ambala", "Bhiwani", "Charkhi Dadri", "Faridabad", "Fatehabad", "Gurugram",
                "Hisar", "Jhajjar", "Jind", "Kaithal", "Karnal", "Kurukshetra", "Mahendragarh",
                "Nuh", "Palwal", "Panchkula", "Panipat", "Rewari", "Rohtak", "Sirsa", "Sonipat", "Yamunanagar"
            ),
            regionalLanguages = listOf(AppLanguage.HINDI, AppLanguage.PUNJABI, AppLanguage.ENGLISH),
            primaryCrops = listOf("Wheat", "Mustard", "Rice", "Cotton", "Sugarcane")
        ),
        StateGeoInfo(
            stateName = "Rajasthan",
            districts = listOf(
                "Ajmer", "Alwar", "Banswara", "Baran", "Barmer", "Bharatpur", "Bhilwara",
                "Bikaner", "Bundi", "Chittorgarh", "Churu", "Dausa", "Dholpur", "Dungarpur",
                "Hanumangarh", "Jaipur", "Jaisalmer", "Jalore", "Jhalawar", "Jhunjhunu", "Jodhpur",
                "Karauli", "Kota", "Nagaur", "Pali", "Pratapgarh", "Rajsamand", "Sawai Madhopur",
                "Sikar", "Sirohi", "Sri Ganganagar", "Tonk", "Udaipur"
            ),
            regionalLanguages = listOf(AppLanguage.HINDI, AppLanguage.ENGLISH),
            primaryCrops = listOf("Mustard", "Bajra", "Wheat", "Gram", "Guar", "Soybean")
        ),
        StateGeoInfo(
            stateName = "Madhya Pradesh",
            districts = listOf(
                "Bhopal", "Indore", "Gwalior", "Jabalpur", "Ujjain", "Sagar", "Dewas",
                "Khargone", "Hoshangabad", "Dhar", "Ratlam", "Chhindwara", "Sehore", "Vidisha",
                "Rewa", "Satna", "Mandsaur", "Neemuch", "Shajapur", "Khandwa", "Shivpuri"
            ),
            regionalLanguages = listOf(AppLanguage.HINDI, AppLanguage.ENGLISH),
            primaryCrops = listOf("Soybean", "Wheat", "Gram", "Cotton", "Garlic", "Mustard")
        ),
        StateGeoInfo(
            stateName = "Uttar Pradesh",
            districts = listOf(
                "Agra", "Aligarh", "Prayagraj", "Varanasi", "Lucknow", "Kanpur", "Meerut",
                "Bareilly", "Moradabad", "Gorakhpur", "Mathura", "Jhansi", "Muzaffarnagar",
                "Bijnor", "Ayodhya", "Budaun", "Barabanki", "Lakhimpur Kheri", "Sitapur", "Hardoi"
            ),
            regionalLanguages = listOf(AppLanguage.HINDI, AppLanguage.URDU, AppLanguage.ENGLISH),
            primaryCrops = listOf("Sugarcane", "Wheat", "Rice", "Potato", "Mustard", "Mentha")
        ),
        StateGeoInfo(
            stateName = "Tamil Nadu",
            districts = listOf(
                "Chennai", "Coimbatore", "Cuddalore", "Dharmapuri", "Dindigul", "Erode",
                "Kanchipuram", "Kanyakumari", "Karur", "Madurai", "Nagapattinam", "Namakkal",
                "Perambalur", "Pudukkottai", "Ramanathapuram", "Salem", "Sivaganga", "Thanjavur",
                "Theni", "Thiruvallur", "Thiruvarur", "Tiruchirappalli", "Tirunelveli", "Tiruppur",
                "Tiruvannamalai", "Vellore", "Viluppuram", "Virudhunagar"
            ),
            regionalLanguages = listOf(AppLanguage.TAMIL, AppLanguage.ENGLISH),
            primaryCrops = listOf("Paddy", "Sugarcane", "Groundnut", "Banana", "Turmeric", "Coconut")
        ),
        StateGeoInfo(
            stateName = "Karnataka",
            districts = listOf(
                "Bagalkote", "Ballari", "Belagavi", "Bengaluru Rural", "Bengaluru Urban", "Bidar",
                "Chamarajanagar", "Chikkaballapura", "Chikkamagaluru", "Chitradurga", "Dakshina Kannada",
                "Davanagere", "Dharwad", "Gadag", "Hassan", "Haveri", "Kalaburagi", "Kodagu",
                "Kolar", "Koppal", "Mandya", "Mysuru", "Raichur", "Ramanagara", "Shivamogga",
                "Tumakuru", "Udupi", "Uttara Kannada", "Vijayapura", "Yadgir"
            ),
            regionalLanguages = listOf(AppLanguage.KANNADA, AppLanguage.ENGLISH),
            primaryCrops = listOf("Ragi", "Rice", "Sugarcane", "Coffee", "Cotton", "Arecanut")
        ),
        StateGeoInfo(
            stateName = "Andhra Pradesh",
            districts = listOf(
                "Anantapur", "Chittoor", "East Godavari", "Guntur", "Krishna", "Kurnool",
                "Prakasam", "SPSR Nellore", "Srikakulam", "Visakhapatnam", "Vizianagaram", "West Godavari", "YSR Kadapa"
            ),
            regionalLanguages = listOf(AppLanguage.TELUGU, AppLanguage.ENGLISH),
            primaryCrops = listOf("Paddy", "Cotton", "Chilli", "Groundnut", "Tobacco", "Sugarcane")
        ),
        StateGeoInfo(
            stateName = "Telangana",
            districts = listOf(
                "Adilabad", "Bhadradri Kothagudem", "Hyderabad", "Jagtial", "Jangaon",
                "Jayashankar Bhupalpally", "Jogulamba Gadwal", "Kamareddy", "Karimnagar", "Khammam",
                "Mahabubabad", "Mahabubnagar", "Mancherial", "Medak", "Medchal", "Nalgonda",
                "Nizamabad", "Rangareddy", "Sangareddy", "Siddipet", "Suryapet", "Vikarabad", "Warangal"
            ),
            regionalLanguages = listOf(AppLanguage.TELUGU, AppLanguage.URDU, AppLanguage.ENGLISH),
            primaryCrops = listOf("Cotton", "Paddy", "Maize", "Chilli", "Turmeric", "Soybean")
        ),
        StateGeoInfo(
            stateName = "Kerala",
            districts = listOf(
                "Alappuzha", "Ernakulam", "Idukki", "Kannur", "Kasaragod", "Kollam",
                "Kottayam", "Kozhikode", "Malappuram", "Palakkad", "Pathanamthitta",
                "Thiruvananthapuram", "Thrissur", "Wayanad"
            ),
            regionalLanguages = listOf(AppLanguage.MALAYALAM, AppLanguage.ENGLISH),
            primaryCrops = listOf("Rubber", "Coconut", "Paddy", "Black Pepper", "Cardamom", "Tea")
        ),
        StateGeoInfo(
            stateName = "West Bengal",
            districts = listOf(
                "Alipurduar", "Bankura", "Birbhum", "Cooch Behar", "Dakshin Dinajpur", "Darjeeling",
                "Hooghly", "Howrah", "Jalpaiguri", "Jhargram", "Kalimpong", "Kolkata", "Malda",
                "Murshidabad", "Nadia", "North 24 Parganas", "Paschim Bardhaman", "Paschim Medinipur",
                "Purba Bardhaman", "Purba Medinipur", "Purulia", "South 24 Parganas", "Uttar Dinajpur"
            ),
            regionalLanguages = listOf(AppLanguage.BENGALI, AppLanguage.ENGLISH),
            primaryCrops = listOf("Rice", "Jute", "Potato", "Tea", "Mustard", "Vegetables")
        ),
        StateGeoInfo(
            stateName = "Bihar",
            districts = listOf(
                "Araria", "Arwal", "Aurangabad", "Banka", "Begusarai", "Bhagalpur", "Bhojpur",
                "Buxar", "Darbhanga", "East Champaran", "Gaya", "Gopalganj", "Jamui", "Jehanabad",
                "Kaimur", "Katihar", "Khagaria", "Kishanganj", "Lakhisarai", "Madhepura", "Madhubani",
                "Munger", "Muzaffarpur", "Nalanda", "Nawada", "Patna", "Purnia", "Rohtas",
                "Saharsa", "Samastipur", "Saran", "Sheikhpura", "Sheohar", "Sitamarhi", "Siwan",
                "Supaul", "Vaishali", "West Champaran"
            ),
            regionalLanguages = listOf(AppLanguage.HINDI, AppLanguage.MAITHILI, AppLanguage.URDU, AppLanguage.ENGLISH),
            primaryCrops = listOf("Rice", "Wheat", "Maize", "Litchi", "Sugarcane", "Pulses")
        ),
        StateGeoInfo(
            stateName = "Odisha",
            districts = listOf(
                "Angul", "Balangir", "Balasore", "Bargarh", "Bhadrak", "Boudh", "Cuttack",
                "Deogarh", "Dhenkanal", "Gajapati", "Ganjam", "Jagatsinghpur", "Jajpur", "Jharsuguda",
                "Kalahandi", "Kandhamal", "Kendrapara", "Kendujhar", "Khordha", "Koraput", "Malkangiri",
                "Mayurbhanj", "Nabarangpur", "Nayagarh", "Nuapada", "Puri", "Rayagada", "Sambalpur",
                "Subarnapur", "Sundargarh"
            ),
            regionalLanguages = listOf(AppLanguage.ODIA, AppLanguage.HINDI, AppLanguage.ENGLISH),
            primaryCrops = listOf("Paddy", "Pulses", "Oilseeds", "Jute", "Sugarcane")
        ),
        StateGeoInfo(
            stateName = "Assam",
            districts = listOf(
                "Baksa", "Barpeta", "Biswanath", "Bongaigaon", "Cachar", "Charaideo", "Chirang",
                "Darrang", "Dhemaji", "Dhubri", "Dibrugarh", "Dima Hasao", "Goalpara", "Golaghat",
                "Hailakandi", "Hojai", "Jorhat", "Kamrup", "Kamrup Metropolitan", "Karbi Anglong",
                "Karimganj", "Kokrajhar", "Lakhimpur", "Majuli", "Morigaon", "Nagaon", "Nalbari",
                "Sivasagar", "Sonitpur", "South Salmara-Mankachar", "Tinsukia", "Udalguri", "West Karbi Anglong"
            ),
            regionalLanguages = listOf(AppLanguage.ASSAMESE, AppLanguage.BENGALI, AppLanguage.BODO, AppLanguage.ENGLISH),
            primaryCrops = listOf("Tea", "Rice", "Jute", "Mustard", "Sugarcane", "Arecanut")
        )
    )

    fun getDistrictsForState(stateName: String): List<String> {
        val found = statesList.firstOrNull { it.stateName.equals(stateName, ignoreCase = true) }
        return found?.districts ?: gujaratDistricts
    }

    fun getRegionalLanguagesForState(stateName: String): List<AppLanguage> {
        val found = statesList.firstOrNull { it.stateName.equals(stateName, ignoreCase = true) }
        return found?.regionalLanguages ?: listOf(AppLanguage.GUJARATI, AppLanguage.HINDI, AppLanguage.ENGLISH)
    }

    fun getAllStateNames(): List<String> {
        return statesList.map { it.stateName }
    }
}
