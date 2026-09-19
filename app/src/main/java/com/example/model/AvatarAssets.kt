package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.R

data class AvatarLayerItem(
    val id: String,
    val name: String,
    val nameAr: String,
    val previewColor: Color,
    val iconEmoji: String = "",
    val drawableRes: Int? = null,
    val description: String = ""
)

object AvatarAssets {

    // ==========================================
    // LAYER 1: BACKGROUNDS
    // ==========================================
    val BACKGROUNDS: Map<String, AvatarLayerItem> = mapOf(
        "bg_garden" to AvatarLayerItem(
            id = "bg_garden",
            name = "Sunny Garden & Fence",
            nameAr = "حديقة الزهور وسور الخشب",
            previewColor = Color(0xFF81C784),
            iconEmoji = "🌸",
            drawableRes = R.drawable.img_avatar_bg_garden,
            description = "حديقة مشمسة مع شجرة الكرز وسور أبيض"
        ),
        "bg_concert" to AvatarLayerItem(
            id = "bg_concert",
            name = "Rock Concert Stage",
            nameAr = "مسرح حفل الروك وناطحات السحاب",
            previewColor = Color(0xFF37474F),
            iconEmoji = "🎸",
            drawableRes = R.drawable.img_avatar_bg_concert,
            description = "مسرح حفلات مضاء مع ناطحات سحاب وجمهور"
        ),
        "bg_nightcity" to AvatarLayerItem(
            id = "bg_nightcity",
            name = "Gothic Night Gates",
            nameAr = "شارع وبوابات قوطية ليلية",
            previewColor = Color(0xFF263238),
            iconEmoji = "🌙",
            drawableRes = R.drawable.img_avatar_bg_nightcity,
            description = "بوابات حديدية أثرية تحت ضوء القمر"
        ),
        "bg_fashion" to AvatarLayerItem(
            id = "bg_fashion",
            name = "VIP Fashion Studio",
            nameAr = "استوديو VIP وأضواء براقة",
            previewColor = Color(0xFFF48FB1),
            iconEmoji = "✨",
            drawableRes = R.drawable.img_avatar_bg_fashion,
            description = "أضواء دافئة وتأثيرات ذهبية وفخمة"
        ),
        "bg_retro_blue" to AvatarLayerItem(
            id = "bg_retro_blue",
            name = "Classic mig33 Sky",
            nameAr = "سماء ميج الكلاسيكية",
            previewColor = Color(0xFF0288D1),
            iconEmoji = "☁️",
            description = "التدرج الأزرق الكلاسيكي لميج"
        ),
        "bg_sunset" to AvatarLayerItem(
            id = "bg_sunset",
            name = "Sunset City",
            nameAr = "غروب المدينة",
            previewColor = Color(0xFFFF7043),
            iconEmoji = "🌅",
            description = "تدرج الغروب البرتقالي الدافئ"
        ),
        "bg_knitted_cozy" to AvatarLayerItem(
            id = "bg_knitted_cozy",
            name = "Knitted Cozy Trellis",
            nameAr = "حديقة خيوط الصوف الدافئة",
            previewColor = Color(0xFF8D6E63),
            iconEmoji = "🧶",
            description = "أشجار محبوكة وموقد دافئ"
        ),
        "bg_abyssal_scene" to AvatarLayerItem(
            id = "bg_abyssal_scene",
            name = "Abyssal Purple Realm",
            nameAr = "العالم البنفسجي الغامض",
            previewColor = Color(0xFF4A148C),
            iconEmoji = "🔮",
            description = "سماء شفق بنفسجية ساحرة"
        )
    )

    // ==========================================
    // LAYER 2: BASE BODY & FACE (Skin & Eyes)
    // ==========================================
    val BASES: Map<String, AvatarLayerItem> = mapOf(
        "base_fair" to AvatarLayerItem(
            id = "base_fair",
            name = "Fair Peach Anime",
            nameAr = "بشرة خوخية فاتحة",
            previewColor = Color(0xFFFFD1C1),
            iconEmoji = "🧑",
            description = "ملامح أنمي شبابية وعيون كرتونية واسعة"
        ),
        "base_warm" to AvatarLayerItem(
            id = "base_warm",
            name = "Warm Honey Tan",
            nameAr = "بشرة عسلية دافئة",
            previewColor = Color(0xFFF0A58A),
            iconEmoji = "🧔",
            description = "بشرة طبيعية دافئة وتفاصيل ناعمة"
        ),
        "base_porcelain" to AvatarLayerItem(
            id = "base_porcelain",
            name = "Porcelain Doll",
            nameAr = "بشرة بورسلين ناصعة",
            previewColor = Color(0xFFFFE8DC),
            iconEmoji = "👧",
            description = "مظهر دمية كلاسيكية مع أحمر خدود"
        ),
        "base_tan" to AvatarLayerItem(
            id = "base_tan",
            name = "Bronze Tan",
            nameAr = "بشرة برونزية",
            previewColor = Color(0xFFC68662),
            iconEmoji = "🧑‍🦱",
            description = "بشرة برونزية صيفية جذابة"
        ),
        "base_gothic" to AvatarLayerItem(
            id = "base_gothic",
            name = "Gothic Pale",
            nameAr = "بشرة قوطية باهتة",
            previewColor = Color(0xFFFFF2EC),
            iconEmoji = "🧛",
            description = "مظهر قوطي كول مع عيون دخانية"
        ),
        "base_aquamarine_blue" to AvatarLayerItem(
            id = "base_aquamarine_blue",
            name = "Blue Aquamarine Eyes",
            nameAr = "عيون أكوامارين زرقاء ساحرة",
            previewColor = Color(0xFF00B0FF),
            iconEmoji = "👁️",
            description = "عيون مانجا زرقاء بكحل ورموش كثيفة"
        ),
        "base_aquamarine_green" to AvatarLayerItem(
            id = "base_aquamarine_green",
            name = "Green Aquamarine Eyes",
            nameAr = "عيون أكوامارين خضراء",
            previewColor = Color(0xFF00E676),
            iconEmoji = "🟢",
            description = "عيون أنمي خضراء براقة بلمعان كريستالي"
        ),
        "base_oneeye_pink" to AvatarLayerItem(
            id = "base_oneeye_pink",
            name = "Pink OneEye Anime Doll",
            nameAr = "عيون وردية كرتونية كيووت",
            previewColor = Color(0xFFFF4081),
            iconEmoji = "💖",
            description = "عيون وردية ناعمة مع رموش سفلية مميزة"
        )
    )

    // ==========================================
    // LAYER 3: CLOTHES & OUTFITS
    // ==========================================
    val CLOTHES: Map<String, AvatarLayerItem> = mapOf(
        "cloth_denim_roses" to AvatarLayerItem(
            id = "cloth_denim_roses",
            name = "Blue Denim Jacket & Jeans",
            nameAr = "جاكيت جينز أزرق وبنطال ممزق",
            previewColor = Color(0xFF2B4C7E),
            iconEmoji = "🧥",
            description = "جاكيت جينز كلاسيكي مع تيشيرت داخلي وحذاء رياضي"
        ),
        "cloth_rocker_leather" to AvatarLayerItem(
            id = "cloth_rocker_leather",
            name = "Rocker Studded Leather",
            nameAr = "جاكيت روك جلدي وحزام نجمة",
            previewColor = Color(0xFF1E1E1E),
            iconEmoji = "🎸",
            description = "جاكيت دراجات أسود مرصع وحزام نجمة حمراء وأحذية روكر"
        ),
        "cloth_pink_coquette_gown" to AvatarLayerItem(
            id = "cloth_pink_coquette_gown",
            name = "Pink Lace Coquette Gown",
            nameAr = "فستان كوكيت وردي دانتيل ومشد",
            previewColor = Color(0xFFF8BBD0),
            iconEmoji = "👗",
            description = "فستان قصير بطبقات دانتيل أسود ومشد فيونكة"
        ),
        "cloth_hikaru_kitty_hoodie" to AvatarLayerItem(
            id = "cloth_hikaru_kitty_hoodie",
            name = "Blackberry Kitty Hoodie",
            nameAr = "هودي القطة السوداء المتدرج",
            previewColor = Color(0xFF37474F),
            iconEmoji = "🐱",
            description = "سويت شيرت متدرج اللون مع طباعة قطة هيكارو"
        ),
        "cloth_blossom_kimono_pink" to AvatarLayerItem(
            id = "cloth_blossom_kimono_pink",
            name = "Pink Blossom Kimono",
            nameAr = "كيمونو أزهار الكرز الوردي",
            previewColor = Color(0xFFFF80AB),
            iconEmoji = "👘",
            description = "كيمونو ياباني أنيق مع أكمام متدلية وحزام عريض"
        ),
        "cloth_blossom_kimono_blue" to AvatarLayerItem(
            id = "cloth_blossom_kimono_blue",
            name = "Blue Blossom Kimono",
            nameAr = "كيمونو الأزهار الأزرق السماوي",
            previewColor = Color(0xFF40C4FF),
            iconEmoji = "👘",
            description = "كيمونو تقليدي أزرق مطرز بنقوش كلاسيكية"
        ),
        "cloth_moroccan_orange" to AvatarLayerItem(
            id = "cloth_moroccan_orange",
            name = "Moroccan Sunset Dress",
            nameAr = "فستان مغربي برتقالي وذهبي",
            previewColor = Color(0xFFFF6D00),
            iconEmoji = "🧡",
            description = "فستان شرقي راقي بحزام ذهبي مطرز"
        ),
        "cloth_moroccan_purple" to AvatarLayerItem(
            id = "cloth_moroccan_purple",
            name = "Moroccan Royal Purple",
            nameAr = "فستان مغربي ملكي بنفسجي",
            previewColor = Color(0xFF7B1FA2),
            iconEmoji = "💜",
            description = "زي مغربي ملكي مطرز بالخيوط الذهبية"
        ),
        "cloth_cute_crochet_cream" to AvatarLayerItem(
            id = "cloth_cute_crochet_cream",
            name = "Cream Boho Crochet Top",
            nameAr = "توب كروشيه بوهيمي كريمي",
            previewColor = Color(0xFFD7CCC8),
            iconEmoji = "🧶",
            description = "توب كروشيه يدوي مع شال شراشيب ناعم"
        ),
        "cloth_good_vs_evil_pink" to AvatarLayerItem(
            id = "cloth_good_vs_evil_pink",
            name = "Good Vs Evil Pink Angel",
            nameAr = "فستان الملاك الوردي والأجنحة",
            previewColor = Color(0xFFFF4081),
            iconEmoji = "🧚",
            description = "فستان أنمي فانتزي بأشرطة متطايرة"
        ),
        "cloth_red_reapers_set" to AvatarLayerItem(
            id = "cloth_red_reapers_set",
            name = "Red Reapers Gothic Set",
            nameAr = "رداء الحاصد القوطي الأحمر",
            previewColor = Color(0xFFB71C1C),
            iconEmoji = "🩸",
            description = "رداء أحمر داكن مع كاب وحذاء عالي الساق"
        ),
        "cloth_san_francisco_tube" to AvatarLayerItem(
            id = "cloth_san_francisco_tube",
            name = "Hip Hop Jeans & Tube Top",
            nameAr = "توب أزرق مع جينز هيب هوب وسلاسل",
            previewColor = Color(0xFF1976D2),
            iconEmoji = "👖",
            description = "طقم شبابي عصري بجينز أزرق وسلاسل معلقة"
        ),
        "cloth_flapper_fur_red" to AvatarLayerItem(
            id = "cloth_flapper_fur_red",
            name = "Red Flapper Fur Jacket",
            nameAr = "جاكيت فراء أحمر فاخر وبوت",
            previewColor = Color(0xFFD32F2F),
            iconEmoji = "🧣",
            description = "جاكيت فراء أحمر مخملي وبوت شتوي كثيف"
        ),
        "cloth_gumiho_gown_purple" to AvatarLayerItem(
            id = "cloth_gumiho_gown_purple",
            name = "Purple Gumiho Mystic Gown",
            nameAr = "فستان الثعلب الأسطوري البنفسجي",
            previewColor = Color(0xFF6A1B9A),
            iconEmoji = "🦊",
            description = "رداء أسطوري بأكمام واسعة وشعارات مضيئة"
        ),
        "cloth_leopard_chic" to AvatarLayerItem(
            id = "cloth_leopard_chic",
            name = "Leopard Chic Crop & Skirt",
            nameAr = "توب فراء وتنورة جلد النمر",
            previewColor = Color(0xFF8D6E63),
            iconEmoji = "🐆",
            description = "توب بني مع تنورة نمرية وجوارب نجوم وبوت فراء"
        ),
        "cloth_gothic_coat" to AvatarLayerItem(
            id = "cloth_gothic_coat",
            name = "Gothic Pinstripe Trench",
            nameAr = "معطف قوطي مقلم ودانتيل",
            previewColor = Color(0xFF212121),
            iconEmoji = "🖤",
            description = "معطف أسود مقلم مع كورسيه دانتيل وأحذية ضخمة"
        ),
        "cloth_street_hoodie" to AvatarLayerItem(
            id = "cloth_street_hoodie",
            name = "Urban Cyan Street Hoodie",
            nameAr = "هودي ستريت وير سماوي",
            previewColor = Color(0xFF0288D1),
            iconEmoji = "🛹",
            description = "هودي عصري واسع مع بنطال كارجو وحذاء رياضي"
        ),
        "cloth_gentleman_suit" to AvatarLayerItem(
            id = "cloth_gentleman_suit",
            name = "Tailored Charcoal Tuxedo",
            nameAr = "بدلة رسمية كلاسيكية وربطة عنق",
            previewColor = Color(0xFF37474F),
            iconEmoji = "👔",
            description = "بدلة سهرة فخمة مع قميص أبيض وربطة عنق سوداء"
        )
    )

    // ==========================================
    // LAYER 4: HAIRSTYLES
    // ==========================================
    val HAIRSTYLES: Map<String, AvatarLayerItem> = mapOf(
        "hair_spiky_brown" to AvatarLayerItem(
            id = "hair_spiky_brown",
            name = "Anime Spiky Brown",
            nameAr = "شعر أنمي بني مسرح للأعلى",
            previewColor = Color(0xFF5D4037),
            iconEmoji = "💇‍♂️",
            description = "قصة ميج الشهيرة مع خصلات حادة متدرجة"
        ),
        "hair_swirl_pompadour_black" to AvatarLayerItem(
            id = "hair_swirl_pompadour_black",
            name = "Black Swirl Pompadour",
            nameAr = "شعر بومبادور ملكي أسود",
            previewColor = Color(0xFF1C1C1C),
            iconEmoji = "🌀",
            description = "تسريحة كلاسيكية مموجة للأعلى بلمعة حريرية"
        ),
        "hair_swirl_pompadour_blaze" to AvatarLayerItem(
            id = "hair_swirl_pompadour_blaze",
            name = "Blaze Fire Pompadour",
            nameAr = "بومبادور اللهب الناري البرتقالي",
            previewColor = Color(0xFFFF5722),
            iconEmoji = "🔥",
            description = "شعر ناري مموج بتدرج أحمر وبرتقالي متوهج"
        ),
        "hair_swirl_pompadour_vanilla" to AvatarLayerItem(
            id = "hair_swirl_pompadour_vanilla",
            name = "Vanilla Zebra Swirl",
            nameAr = "بومبادور الفانيليا المخطط",
            previewColor = Color(0xFFFFE082),
            iconEmoji = "🦓",
            description = "خصلات فانيليا ذهبية ممزوجة بخطوط سوداء"
        ),
        "hair_dealuxe_silver" to AvatarLayerItem(
            id = "hair_dealuxe_silver",
            name = "Silver Dealuxe Undercut",
            nameAr = "شعر ديلوكس فضي جانبي",
            previewColor = Color(0xFFCFD8DC),
            iconEmoji = "💎",
            description = "قصة شبابية فضية فاخرة بجوانب محلوقة"
        ),
        "hair_clean_cut_polar" to AvatarLayerItem(
            id = "hair_clean_cut_polar",
            name = "Polar Ice Clean Cut",
            nameAr = "شعر الجليد القطبي الأزرق السماوي",
            previewColor = Color(0xFF4FC3F7),
            iconEmoji = "❄️",
            description = "تموجات زرقاء جليدية ناصعة ومسرحة بإتقان"
        ),
        "hair_dreadlocks_black" to AvatarLayerItem(
            id = "hair_dreadlocks_black",
            name = "Black Knotted Dreadlocks",
            nameAr = "ضفائر دريدلوكس معقودة سوداء",
            previewColor = Color(0xFF212121),
            iconEmoji = "🧶",
            description = "جدائل دريدلوكس أفريقية كثيفة وأنيقة"
        ),
        "hair_harajuku_pigtails" to AvatarLayerItem(
            id = "hair_harajuku_pigtails",
            name = "Glowing Neon Harajuku",
            nameAr = "قرنين هاراجوكو نيون متدرج",
            previewColor = Color(0xFFE040FB),
            iconEmoji = "👧",
            description = "قرنان وردي وأزرق مع غرة متدرجة"
        ),
        "hair_high_bow_brown" to AvatarLayerItem(
            id = "hair_high_bow_brown",
            name = "Brown High Bow Buns",
            nameAr = "كعكتان مع فيونكة شعر بنية",
            previewColor = Color(0xFF6D4C41),
            iconEmoji = "🎀",
            description = "شعر بني مرفوع بشكل فيونكة علوية ساحرة"
        ),
        "hair_beverly_hills_spiked" to AvatarLayerItem(
            id = "hair_beverly_hills_spiked",
            name = "Beverly Hills Spiked Wavy",
            nameAr = "شعر بيفرلي هيلز المسرح",
            previewColor = Color(0xFF263238),
            iconEmoji = "🌊",
            description = "خصلات كثيفة مائلة للأعلى بأسلوب نجوم السينما"
        ),
        "hair_rainbow_academia" to AvatarLayerItem(
            id = "hair_rainbow_academia",
            name = "Rainbow Academia Multi-Hue",
            nameAr = "شعر ألوان الطيف الأكاديمي",
            previewColor = Color(0xFFFF1744),
            iconEmoji = "🌈",
            description = "تسريحة متعددة الألوان متدرجة بجميع درجات الطيف"
        ),
        "hair_fae_lord_cherry" to AvatarLayerItem(
            id = "hair_fae_lord_cherry",
            name = "Cherry Blossom Fae Lord",
            nameAr = "شعر أمير أزهار الكرز",
            previewColor = Color(0xFFFF4081),
            iconEmoji = "🌸",
            description = "شعر مخطط بالوردي والأبيض بخصلات حادة"
        ),
        "hair_undercut_superstar" to AvatarLayerItem(
            id = "hair_undercut_superstar",
            name = "Superstar Teal Undercut",
            nameAr = "أندر كت سوبر ستار تركواز",
            previewColor = Color(0xFF00BFA5),
            iconEmoji = "🌟",
            description = "قصة عصرية مع خصلات تركوازية متداخلة بالأسود"
        ),
        "hair_vampress_black" to AvatarLayerItem(
            id = "hair_vampress_black",
            name = "Black Vampiress Locks",
            nameAr = "شعر مصاصة الدماء الطويل والأسود",
            previewColor = Color(0xFF000000),
            iconEmoji = "🧛‍♀️",
            description = "شعر أسود فاحم ينسدل للأسفل مع غرة مستقيمة"
        ),
        "hair_beanie_black" to AvatarLayerItem(
            id = "hair_beanie_black",
            name = "Black Knit Beanie & Bangs",
            nameAr = "طاقية صوف سوداء وخصلات أمامية",
            previewColor = Color(0xFF212121),
            iconEmoji = "🧢",
            description = "قبعة شتوية عصرية مع خصلات شعر بنية على الجبين"
        ),
        "hair_wavy_brunette" to AvatarLayerItem(
            id = "hair_wavy_brunette",
            name = "Long Flowing Wavy Brunette",
            nameAr = "شعر بني مموج طويل وطوق أنيق",
            previewColor = Color(0xFF3E2723),
            iconEmoji = "💇‍♀️",
            description = "خصلات شعر كثيفة مموجة تنسدل بنعومة على الكتفين"
        ),
        "hair_wolf_fluffy" to AvatarLayerItem(
            id = "hair_wolf_fluffy",
            name = "Fluffy Wolf-Cut & Ear Tufts",
            nameAr = "قصة الذئب المنفوشة مع آذان قطة",
            previewColor = Color(0xFF8D6E63),
            iconEmoji = "🐺",
            description = "شعر كستنائي كثيف ومفصل مع دبابيس فراشة"
        ),
        "hair_emo_fringe" to AvatarLayerItem(
            id = "hair_emo_fringe",
            name = "Midnight Emo Fringe",
            nameAr = "شعر إيمو أسود لامع مائل",
            previewColor = Color(0xFF1A1A1A),
            iconEmoji = "✂️",
            description = "قصة كلاسيكية سوداء مائلة مع لمعان ناعم"
        ),
        "hair_blonde_short" to AvatarLayerItem(
            id = "hair_blonde_short",
            name = "Layered Golden Blonde",
            nameAr = "شعر أشقر ذهبي متدرج",
            previewColor = Color(0xFFFFD54F),
            iconEmoji = "👱",
            description = "شعر أشقر حيوي مع خصلات مضيئة"
        ),
        "hair_cyber_ponytail" to AvatarLayerItem(
            id = "hair_cyber_ponytail",
            name = "Cyberpunk Pink High Ponytail",
            nameAr = "ذيل حصان وردي سايبربانك",
            previewColor = Color(0xFFE91E63),
            iconEmoji = "🎀",
            description = "شعر وردي فاقع مع قصة غرة مستقيمة"
        )
    )

    // ==========================================
    // LAYER 5: ACCESSORIES, EYEWEAR & PROPS
    // ==========================================
    val ACCESSORIES: Map<String, AvatarLayerItem> = mapOf(
        "acc_mutator_goggles_green" to AvatarLayerItem(
            id = "acc_mutator_goggles_green",
            name = "Green Mutator Head Goggles",
            nameAr = "نظارات الرأس الخضراء المتحولة",
            previewColor = Color(0xFF2E7D32),
            iconEmoji = "🥽",
            description = "نظارات سباق خضراء مرفوعة على الرأس"
        ),
        "acc_mutator_goggles_white" to AvatarLayerItem(
            id = "acc_mutator_goggles_white",
            name = "White Mutator Head Goggles",
            nameAr = "نظارات الرأس الفضية المستقبلية",
            previewColor = Color(0xFFECEFF1),
            iconEmoji = "🥽",
            description = "نظارات مستقبلية بيضاء بإطار كروم"
        ),
        "acc_heart_purse_red" to AvatarLayerItem(
            id = "acc_heart_purse_red",
            name = "Red Paigeyy Heart Purse",
            nameAr = "حقيبة القلب الأحمر وسلسلة فضية",
            previewColor = Color(0xFFD32F2F),
            iconEmoji = "❤️",
            description = "حقيبة يد جلدية بشكل قلب أحمر نابض"
        ),
        "acc_heart_purse_blue" to AvatarLayerItem(
            id = "acc_heart_purse_blue",
            name = "Blue Paigeyy Heart Purse",
            nameAr = "حقيبة القلب الأزرق الكيوت",
            previewColor = Color(0xFF1976D2),
            iconEmoji = "💙",
            description = "حقيبة قلب أزرق داكن وسلسلة معدنية"
        ),
        "acc_heart_purse_white" to AvatarLayerItem(
            id = "acc_heart_purse_white",
            name = "White Paigeyy Heart Purse",
            nameAr = "حقيبة القلب الأبيض واللؤلؤ",
            previewColor = Color(0xFFFAFAFA),
            iconEmoji = "🤍",
            description = "حقيبة بيضاء على شكل قلب مع قلب أحمر صغير"
        ),
        "acc_selfcare_shades_purple" to AvatarLayerItem(
            id = "acc_selfcare_shades_purple",
            name = "Purple Self Care Shades",
            nameAr = "نظارات الموضة البنفسجية",
            previewColor = Color(0xFFAB47BC),
            iconEmoji = "🕶️",
            description = "نظارات كلاسيكية بإطار بيج وعدسات بنفسجية"
        ),
        "acc_selfcare_shades_blue" to AvatarLayerItem(
            id = "acc_selfcare_shades_blue",
            name = "Blue Self Care Shades",
            nameAr = "نظارات الصيف الزرقاء السماوية",
            previewColor = Color(0xFF42A5F5),
            iconEmoji = "🕶️",
            description = "نظارات بإطار سماوي وعدسات سوداء عاكسة"
        ),
        "acc_lace_bandana_rose" to AvatarLayerItem(
            id = "acc_lace_bandana_rose",
            name = "Mist Rose Lace Bandana",
            nameAr = "بندانة الدانتيل الوردي المعقودة",
            previewColor = Color(0xFFFFAB91),
            iconEmoji = "🧕",
            description = "بندانة رأس مطرزة بالورود الدقيقة مع عقدة سفلية"
        ),
        "acc_lace_bandana_black" to AvatarLayerItem(
            id = "acc_lace_bandana_black",
            name = "Black Boutique Lace Bandana",
            nameAr = "بندانة دانتيل سوداء قوطية",
            previewColor = Color(0xFF212121),
            iconEmoji = "🖤",
            description = "بندانة رأس من الدانتيل الأسود الفاخر"
        ),
        "acc_glam_bow_belt_red" to AvatarLayerItem(
            id = "acc_glam_bow_belt_red",
            name = "Red Pierre Glam Bow Belt",
            nameAr = "حزام فيونكة ساتان أحمر",
            previewColor = Color(0xFFD50000),
            iconEmoji = "🎀",
            description = "حزام فيونكة ستان طويل يتدلى بأناقة"
        ),
        "acc_glam_bow_belt_pink" to AvatarLayerItem(
            id = "acc_glam_bow_belt_pink",
            name = "Pink Pierre Glam Bow Belt",
            nameAr = "حزام فيونكة وردي لامع",
            previewColor = Color(0xFFFF4081),
            iconEmoji = "🎀",
            description = "فيونكة وسط وردية حريرية للمناسبات"
        ),
        "acc_cyber_headphones_green" to AvatarLayerItem(
            id = "acc_cyber_headphones_green",
            name = "Green Cyber Hacker Headphones",
            nameAr = "سماعات هاكر سايبر الخضراء المضيئة",
            previewColor = Color(0xFF76FF03),
            iconEmoji = "🎧",
            description = "سماعات رأس احترافية مع إضاءة نيون خضراء"
        ),
        "acc_broken_heart_horns" to AvatarLayerItem(
            id = "acc_broken_heart_horns",
            name = "Black Broken Heart Horns",
            nameAr = "قرون القلب المكسور السوداء المرصعة",
            previewColor = Color(0xFF212121),
            iconEmoji = "😈",
            description = "طوق قرون قوطية مرصعة بالكريستال اللامع"
        ),
        "acc_angel_halo_silver" to AvatarLayerItem(
            id = "acc_angel_halo_silver",
            name = "Silver Angel Halo & Tiara",
            nameAr = "هالة الملاك الفضية المرصعة",
            previewColor = Color(0xFFE0F7FA),
            iconEmoji = "😇",
            description = "تاج وهالة فضية تعلو الرأس ببريق ماسي"
        ),
        "acc_disco_piano_tote" to AvatarLayerItem(
            id = "acc_disco_piano_tote",
            name = "Disco Piano Keys Tote",
            nameAr = "حقيبة البيانو الموسيقية",
            previewColor = Color(0xFF1E1E1E),
            iconEmoji = "🎹",
            description = "حقيبة يد جلدية سوداء بتصميم مفاتيح بيانو"
        ),
        "acc_kawaii_kitty_purse" to AvatarLayerItem(
            id = "acc_kawaii_kitty_purse",
            name = "Kawaii Kitty Face Purse",
            nameAr = "حقيبة القطة الكيوت الوردية",
            previewColor = Color(0xFFFF80AB),
            iconEmoji = "👛",
            description = "حقيبة صغيرة بشكل وجه قطة وأذنين لطيفة"
        ),
        "acc_gem_crown_gold" to AvatarLayerItem(
            id = "acc_gem_crown_gold",
            name = "Royal Jewel and Gem Crown",
            nameAr = "تاج الملوك الذهبي المرصع بالجواهر",
            previewColor = Color(0xFFFFD54F),
            iconEmoji = "👑",
            description = "تاج ذهبي فخم مرصع بأحجار الياقوت والزمرد"
        ),
        "acc_cupids_bow_pink" to AvatarLayerItem(
            id = "acc_cupids_bow_pink",
            name = "Pink Cupid's Bow & Arrow",
            nameAr = "قوس وسهم كيوبيد الوردي",
            previewColor = Color(0xFFFF4081),
            iconEmoji = "🏹",
            description = "قوس حب وردي مع رأس سهم على شكل قلب"
        ),
        "acc_zeus_lightning" to AvatarLayerItem(
            id = "acc_zeus_lightning",
            name = "Zeus Electric Lightning Scepter",
            nameAr = "صولجان صاعقة زيوس الكهربائية",
            previewColor = Color(0xFF00E5FF),
            iconEmoji = "⚡",
            description = "صاعقة برق زرقاء مضيئة تفرغ طاقة سحرية"
        ),
        "acc_lollipop_mouth" to AvatarLayerItem(
            id = "acc_lollipop_mouth",
            name = "Sweet Red Spiral Lollipop",
            nameAr = "مصاصة حلوى حمراء بالفم",
            previewColor = Color(0xFFE53935),
            iconEmoji = "🍭",
            description = "مصاصة حلزونية حمراء في الفم"
        ),
        "acc_gum_bubble" to AvatarLayerItem(
            id = "acc_gum_bubble",
            name = "Animated Bubble Gum",
            nameAr = "فقاعة علكة بنفسجية منفوخة",
            previewColor = Color(0xFFBA68C8),
            iconEmoji = "🫧",
            description = "فقاعة لبان بنفسجية براقة أمام الفم"
        ),
        "acc_blue_aviators" to AvatarLayerItem(
            id = "acc_blue_aviators",
            name = "Blue Aviator Sunglasses",
            nameAr = "نظارة أفياتور زرقاء عاكسة",
            previewColor = Color(0xFF0288D1),
            iconEmoji = "🕶️",
            description = "نظارة شمسية بإطار معدني وعدسات زرقاء متدرجة"
        ),
        "acc_dark_shades" to AvatarLayerItem(
            id = "acc_dark_shades",
            name = "Celebrity Black Shades",
            nameAr = "نظارة شمسية سوداء كلاسيكية",
            previewColor = Color(0xFF212121),
            iconEmoji = "🕶️",
            description = "نظارة مشاهير سوداء بإطار أنيق"
        ),
        "acc_round_glasses" to AvatarLayerItem(
            id = "acc_round_glasses",
            name = "Round Aesthetic Spectacles",
            nameAr = "نظارة دائرية طبية شفافة",
            previewColor = Color(0xFF78909C),
            iconEmoji = "👓",
            description = "نظارة دائرية بإطار فضي ولمعة زجاجية"
        ),
        "acc_v_guitar" to AvatarLayerItem(
            id = "acc_v_guitar",
            name = "Flying-V Electric Guitar",
            nameAr = "جيتار روك V الكهربائي",
            previewColor = Color(0xFFE53935),
            iconEmoji = "🎸",
            description = "جيتار روك كهربائي محمول بحزام جلدي"
        ),
        "acc_roses_bouquet" to AvatarLayerItem(
            id = "acc_roses_bouquet",
            name = "Blooming Pink Roses Bouquet",
            nameAr = "باقة ورد جوري وردي",
            previewColor = Color(0xFFE91E63),
            iconEmoji = "💐",
            description = "باقة ورود وردية يانعة ممسوكة باليد"
        ),
        "acc_gold_chain" to AvatarLayerItem(
            id = "acc_gold_chain",
            name = "Chunky Cuban Gold Chain",
            nameAr = "سلسلة ذهبية عريضة مع نجمة",
            previewColor = Color(0xFFFFD54F),
            iconEmoji = "🪙",
            description = "سلسلة ذهبية لامعة مع قلادة نجمة"
        ),
        "none" to AvatarLayerItem(
            id = "none",
            name = "None",
            nameAr = "بدون إكسسوار",
            previewColor = Color.Transparent,
            iconEmoji = "🚫",
            description = "بدون أي إكسسوار إضافي"
        )
    )

    // ==========================================
    // LAYER 6: PETS, FOREGROUND & STAGE EFFECTS
    // ==========================================
    val PETS_AND_FX: Map<String, AvatarLayerItem> = mapOf(
        "pet_holographic_dragon" to AvatarLayerItem(
            id = "pet_holographic_dragon",
            name = "Holographic Chibi Dragon",
            nameAr = "تنين شيبي الهولوجرامي الأسطوري",
            previewColor = Color(0xFFB388FF),
            iconEmoji = "🐉",
            description = "تنين بلوري صغير بقرون وأجنحة بنفسجية ووردية"
        ),
        "pet_chibi_shiba_black" to AvatarLayerItem(
            id = "pet_chibi_shiba_black",
            name = "Black Chibi Shiba Yober",
            nameAr = "كلب شيبا شيبي أسود",
            previewColor = Color(0xFF37474F),
            iconEmoji = "🐕",
            description = "جرو شيبا إينو أسود ذو حواجب مرحة وذيل ملتف"
        ),
        "pet_chibi_shiba_tan" to AvatarLayerItem(
            id = "pet_chibi_shiba_tan",
            name = "Tan Chibi Shiba Yober",
            nameAr = "كلب شيبا إينو ذهبي مرح",
            previewColor = Color(0xFFFFB74D),
            iconEmoji = "🐕",
            description = "كلب شيبا لطيف باللون البرتقالي الدافئ"
        ),
        "pet_rakoochoo" to AvatarLayerItem(
            id = "pet_rakoochoo",
            name = "Rakoochoo Plushie Yober",
            nameAr = "دمية الراكون اللطيفة راكوتشو",
            previewColor = Color(0xFF8D6E63),
            iconEmoji = "🦝",
            description = "راكون أنمي بقناع أسود وذيل مخطط يقف بجانبك"
        ),
        "pet_oneeye_bear" to AvatarLayerItem(
            id = "pet_oneeye_bear",
            name = "Grey Coat Bear Yober",
            nameAr = "دب قطبي بمعطف رمادي",
            previewColor = Color(0xFFCFD8DC),
            iconEmoji = "🐻",
            description = "دب أبيض صغير يرتدي معطف صوف رمادي"
        ),
        "pet_gumiho_black" to AvatarLayerItem(
            id = "pet_gumiho_black",
            name = "Black 9-Tail Fox Gumiho",
            nameAr = "الثعلب الأسود ذو التسعة أذيال",
            previewColor = Color(0xFF212121),
            iconEmoji = "🦊",
            description = "ثعلب غوميهو أسطوري بتسعة أذيال سوداء وعيون متوهجة"
        ),
        "pet_crypt_wyvern" to AvatarLayerItem(
            id = "pet_crypt_wyvern",
            name = "Monster Crypt Wyvern",
            nameAr = "تنين التنين الصغير المجنح",
            previewColor = Color(0xFF303F9F),
            iconEmoji = "🦇",
            description = "تنين صغير مجنح بأجنحة خفاشية وعيون براقة"
        ),
        "pet_tabby_cat" to AvatarLayerItem(
            id = "pet_tabby_cat",
            name = "Cute Brown Tabby Kitten",
            nameAr = "قطة بنية مخططة لطيفة",
            previewColor = Color(0xFF8D6E63),
            iconEmoji = "🐱",
            description = "قطة بنية صغيرة تجلس بجانبك بسعادة"
        ),
        "pet_mini_husky" to AvatarLayerItem(
            id = "pet_mini_husky",
            name = "Playful Mini Husky Pup",
            nameAr = "جرو هاسكي مرح",
            previewColor = Color(0xFF546E7A),
            iconEmoji = "🐶",
            description = "جرو هاسكي ذو عيون زرقاء مرحة"
        ),
        "fx_math_mastermind" to AvatarLayerItem(
            id = "fx_math_mastermind",
            name = "Math Mastermind Floating Formulas",
            nameAr = "معادلات العبقري الرياضي العائمة",
            previewColor = Color(0xFFB0BEC5),
            iconEmoji = "📐",
            description = "رموز تكامل ومعادلات علمية متطايرة حول الشخصية"
        ),
        "fx_confetti_hearts" to AvatarLayerItem(
            id = "fx_confetti_hearts",
            name = "Floating Hearts & Confetti",
            nameAr = "قلوب حب وكونفيتي متطايرة",
            previewColor = Color(0xFFFF4081),
            iconEmoji = "💕",
            description = "قلوب حمراء وزينة احتفالية تتساقط بنعومة"
        ),
        "fx_elysian_nimbus" to AvatarLayerItem(
            id = "fx_elysian_nimbus",
            name = "Elysian Nimbus Blue Aura",
            nameAr = "هالة الضياء السماوي الزرقاء",
            previewColor = Color(0xFF00E5FF),
            iconEmoji = "✨",
            description = "حلقة سداسية ونجمة مضيئة محيطة بالشخصية"
        ),
        "fx_rainbow_rays" to AvatarLayerItem(
            id = "fx_rainbow_rays",
            name = "Cosmic Rainbow Rays Effect",
            nameAr = "أشعة قوس قزح الكونية",
            previewColor = Color(0xFFFFEB3B),
            iconEmoji = "🌈",
            description = "أشعة طيف ملونة تنبعث من خلف الأفاتار"
        ),
        "fx_bat_wings_red" to AvatarLayerItem(
            id = "fx_bat_wings_red",
            name = "Spellbound Red Bat Wings",
            nameAr = "أجنحة الوطواط القوطية الحمراء",
            previewColor = Color(0xFFB71C1C),
            iconEmoji = "🦇",
            description = "زوج أجنحة خفاش جلدية حمراء تمتد على الجانبين"
        ),
        "fx_concert_crowd" to AvatarLayerItem(
            id = "fx_concert_crowd",
            name = "Cheering Concert Crowd & Speakers",
            nameAr = "مكبرات صوت وجمهور الحفل",
            previewColor = Color(0xFFD32F2F),
            iconEmoji = "📢",
            description = "سماعات مسرح ضخمة وجمهور يلوح بيديه"
        ),
        "fx_vip_star" to AvatarLayerItem(
            id = "fx_vip_star",
            name = "VIP 'I'M A STAR' Floating Ribbon",
            nameAr = "شعار النجمة الذهبي VIP",
            previewColor = Color(0xFFFFD54F),
            iconEmoji = "⭐",
            description = "وسام نجمة ميج الذهبي مع نجوم مضيئة"
        ),
        "fx_butterflies_aura" to AvatarLayerItem(
            id = "fx_butterflies_aura",
            name = "Glowing Crystal Butterflies",
            nameAr = "فراشات بلورية متوهجة",
            previewColor = Color(0xFF80DEEA),
            iconEmoji = "🦋",
            description = "فراشات مضيئة وهالة سحرية محيطة"
        ),
        "none" to AvatarLayerItem(
            id = "none",
            name = "None",
            nameAr = "بدون مرافق أو تأثير",
            previewColor = Color.Transparent,
            iconEmoji = "🚫",
            description = "بدون أي مرافق أو مؤثر أمامي"
        )
    )

    fun getBackground(id: String): AvatarLayerItem =
        BACKGROUNDS[id] ?: BACKGROUNDS["bg_garden"] ?: BACKGROUNDS.values.first()

    fun getBase(id: String): AvatarLayerItem =
        BASES[id] ?: BASES["base_fair"] ?: BASES.values.first()

    fun getClothes(id: String): AvatarLayerItem =
        CLOTHES[id] ?: CLOTHES["cloth_denim_roses"] ?: CLOTHES.values.first()

    fun getHair(id: String): AvatarLayerItem =
        HAIRSTYLES[id] ?: HAIRSTYLES["hair_spiky_brown"] ?: HAIRSTYLES.values.first()

    fun getAccessory(id: String): AvatarLayerItem? =
        ACCESSORIES[id]?.takeIf { it.id != "none" }

    fun getPetOrFx(id: String): AvatarLayerItem? =
        PETS_AND_FX[id]?.takeIf { it.id != "none" }

    val PETS: Map<String, AvatarLayerItem> = PETS_AND_FX

    val PRESETS: List<MigPresetAvatar> = listOf(
        MigPresetAvatar(
            id = "mig_shadow_tuxedo",
            name = "Shadow Lord Tuxedo",
            nameAr = "سيد الظلال: البدلة الفاخرة والهالة البنفسجية",
            drawableRes = R.drawable.img_avatar_shadow_tuxedo,
            badgeLabel = "Dark VIP",
            description = "بدلة تاكسيدو ثلاثية القطع ومعطف رسمي ملقى على الأكتاف مع عيون متوهجة وهالة ظلال بنفسجية محيطة",
            config = AvatarConfig(
                presetId = "mig_shadow_tuxedo",
                customImageRes = R.drawable.img_avatar_shadow_tuxedo,
                background = "bg_nightcity",
                clothes = "cloth_shadow_tuxedo",
                hair = "hair_emo_black",
                accessory = "acc_purple_aura",
                pet = "none"
            )
        ),
        MigPresetAvatar(
            id = "mig_blood_moon",
            name = "Blood Moon Eclipse Reaper",
            nameAr = "شبح القمر الدموي والرداء الممزق",
            drawableRes = R.drawable.img_avatar_blood_moon,
            badgeLabel = "Eclipse Master",
            description = "رداء مقنع ممزق بحواف متوهجة باللون الأحمر، عيون جمرية ساطعة تحت القمر الدموي العملاق وشجرة البونساي القرمزية",
            config = AvatarConfig(
                presetId = "mig_blood_moon",
                customImageRes = R.drawable.img_avatar_blood_moon,
                background = "bg_nightcity",
                clothes = "cloth_blood_moon_cloak",
                hair = "hair_emo_black",
                accessory = "acc_red_cross",
                pet = "none"
            )
        ),
        MigPresetAvatar(
            id = "mig_kagero_rebel",
            name = "Kagero Tokyo Rebel",
            nameAr = "كاجيرو: متمرد طوكيو بالجاكيت الأحمر والتاتو",
            drawableRes = R.drawable.img_avatar_kagero_rebel,
            badgeLabel = "Street Legend",
            description = "جاكيت بومبر قرمزي بوشم التنين والجمجمة وبنطال السلاسل التكتيكية مع وشم التنين على الرقبة في شوارع طوكيو الماطرة",
            config = AvatarConfig(
                presetId = "mig_kagero_rebel",
                customImageRes = R.drawable.img_avatar_kagero_rebel,
                background = "bg_nightcity",
                clothes = "cloth_kagero_bomber",
                hair = "hair_emo_black",
                accessory = "acc_cyber_headphones",
                pet = "none"
            )
        ),
        MigPresetAvatar(
            id = "mig_pierrot_jester",
            name = "Gothic Pierrot Jester",
            nameAr = "المهرج القوطي وبطاقات اللعب الملكية",
            drawableRes = R.drawable.img_avatar_pierrot_jester,
            badgeLabel = "Baroque VIP",
            description = "بدلة الذيل المخملية بقطع الألماس الأحمر وبطاقات البوكر الملكية وابتسامة المهرج الغامضة على مسرح السيرك الملكي",
            config = AvatarConfig(
                presetId = "mig_pierrot_jester",
                customImageRes = R.drawable.img_avatar_pierrot_jester,
                background = "bg_fashion",
                clothes = "cloth_pierrot_tailcoat",
                hair = "hair_wavy_pompadour_black",
                accessory = "acc_heart_bag_red",
                pet = "none"
            )
        ),
        MigPresetAvatar(
            id = "mig_shinya_flame",
            name = "Shinya Flame Spirit",
            nameAr = "شينيا: روح اللهب وعيون الجمر والوشم",
            drawableRes = R.drawable.img_avatar_shinya_flame,
            badgeLabel = "Elemental VIP",
            description = "شعر اللهب المتوقد وعيون الجمر مع قميص الكتف الأبيض المنسدل ووشم الزهور وسوار الأصداف على شاطئ الأعماق الساحر",
            config = AvatarConfig(
                presetId = "mig_shinya_flame",
                customImageRes = R.drawable.img_avatar_shinya_flame,
                background = "bg_abyssal_scene",
                clothes = "cloth_shinya_draped",
                hair = "hair_pompadour_orange",
                accessory = "acc_seashell_bracelet",
                pet = "none"
            )
        ),
        MigPresetAvatar(
            id = "mig_shadow_lord",
            name = "Shadow Monarch Assassin",
            nameAr = "سيد الظلال والمعطف الأسود الطويل",
            drawableRes = R.drawable.img_avatar_shadow_lord,
            badgeLabel = "Dark Monarch",
            description = "معطف ترينش جلدي أسود طويل وهاي-نك مع هالة تنين الظلال الحبرية المتصاعدة في سماء المدينة القوطية الغائمة",
            config = AvatarConfig(
                presetId = "mig_shadow_lord",
                customImageRes = R.drawable.img_avatar_shadow_lord,
                background = "bg_nightcity",
                clothes = "cloth_shadow_duster",
                hair = "hair_emo_black",
                accessory = "acc_silver_pendant",
                pet = "none"
            )
        ),
        MigPresetAvatar(
            id = "mig_cyber_assassin",
            name = "Crimson Vampire Assassin Queen",
            nameAr = "ملكة القتلة ومصاصي الدماء بالمعطف الجلدي",
            drawableRes = R.drawable.img_avatar_cyber_assassin,
            badgeLabel = "Vamp Queen",
            description = "معطف جلدي مسنن ببطانة حمراء حريرية، كورسيه وبوت الساق العالي وشباك صيد الأسماك في قصر الكاتدرائية القوطية",
            config = AvatarConfig(
                presetId = "mig_cyber_assassin",
                customImageRes = R.drawable.img_avatar_cyber_assassin,
                background = "bg_nightcity",
                clothes = "cloth_vamp_leather_duster",
                hair = "hair_emo_black",
                accessory = "acc_diamond_choker",
                pet = "none"
            )
        ),
        MigPresetAvatar(
            id = "mig_holo_noir_couture",
            name = "Holographic Noir Butterfly Gown",
            nameAr = "فستان الهولوغرافيك الأسود وأجنحة الفراشة",
            drawableRes = R.drawable.img_avatar_holo_noir_couture,
            badgeLabel = "Couture VIP",
            description = "فستان كوتور أسود فاخر بكورسيه مطرز وطبقات شيفون هولوغرافيك قوس قزح مع تاج الأنتلر الكريستالي وشعر فضي حريري",
            config = AvatarConfig(
                presetId = "mig_holo_noir_couture",
                customImageRes = R.drawable.img_avatar_holo_noir_couture,
                background = "bg_fashion",
                clothes = "cloth_holo_noir_couture",
                hair = "hair_wavy_pompadour_black",
                accessory = "acc_tiara_royal",
                pet = "fx_butterflies_aura"
            )
        ),
        MigPresetAvatar(
            id = "mig_crimson_rose",
            name = "Crimson Rose Fantasy",
            nameAr = "أميرة الورد الجوري الأحمر الفاخر",
            drawableRes = R.drawable.img_avatar_crimson_rose,
            badgeLabel = "Royalty VIP",
            description = "فستان الورد الجوري الأحمر الملكي وتطريز الذهب مع التاج الياقوتي وبتلات الورد المتطايرة",
            config = AvatarConfig(
                presetId = "mig_crimson_rose",
                customImageRes = R.drawable.img_avatar_crimson_rose,
                background = "bg_garden",
                clothes = "cloth_crimson_rose_gown",
                hair = "hair_wavy_pompadour_black",
                accessory = "acc_ruby_tiara_gold",
                pet = "none"
            )
        ),
        MigPresetAvatar(
            id = "mig_sapphire_empress",
            name = "Sapphire Ocean Empress",
            nameAr = "إمبراطورة الياقوت الأزرق والفراشات",
            drawableRes = R.drawable.img_avatar_sapphire_empress,
            badgeLabel = "Ultra Luxury",
            description = "فستان الياقوت الأزرق الخيالي والحرير المتدرج مع فراشات بلورية متوهجة والتاج الماسي الملكي",
            config = AvatarConfig(
                presetId = "mig_sapphire_empress",
                customImageRes = R.drawable.img_avatar_sapphire_empress,
                background = "bg_fashion",
                clothes = "cloth_sapphire_empress_gown",
                hair = "hair_spiky_brown",
                accessory = "acc_sapphire_crown_silver",
                pet = "fx_butterflies_aura"
            )
        ),
        MigPresetAvatar(
            id = "mig_dark_angel_punk",
            name = "Dark Angel Y2K Cyber Goth",
            nameAr = "طقم دارك أنجل جوتك وبانك",
            drawableRes = R.drawable.img_avatar_dark_angel,
            badgeLabel = "Y2K Rebel",
            description = "كورسيه أجنحة الدانتيل مع بنطال السلاسل التارتان الفضفاض وبوت المنصة وسلاسل النجوم والمجوهرات",
            config = AvatarConfig(
                presetId = "mig_dark_angel_punk",
                customImageRes = R.drawable.img_avatar_dark_angel,
                background = "bg_nightcity",
                clothes = "cloth_dark_angel_punk",
                hair = "hair_emo_black",
                accessory = "acc_cyber_headphones",
                pet = "none"
            )
        ),
        MigPresetAvatar(
            id = "mig_holo_princess",
            name = "Holographic Iridescent Princess",
            nameAr = "أميرة الفراشة الهولوغرافية الساحرة",
            drawableRes = R.drawable.img_avatar_holo_princess,
            badgeLabel = "Ethereal VIP",
            description = "فستان هولوغرافي بألوان الطيف الشفافة مع تاج الكريستال المتلألئ وبريق النجوم الساحر",
            config = AvatarConfig(
                presetId = "mig_holo_princess",
                customImageRes = R.drawable.img_avatar_holo_princess,
                background = "bg_fashion",
                clothes = "cloth_holo_princess_gown",
                hair = "hair_harajuku_horns",
                accessory = "acc_tiara_royal",
                pet = "fx_butterflies_aura"
            )
        ),
        MigPresetAvatar(
            id = "mig_cyber_techwear",
            name = "Violet Cyber Techwear",
            nameAr = "طقم تيكوير السايبر البنفسجي",
            drawableRes = R.drawable.img_avatar_cyber_techwear,
            badgeLabel = "Tech Street",
            description = "جاكيت ويندبريكر فضفاض وأحزمة تكتيكية مع شورت عالي وسنيكرز سايبر ونظارة فيزر نيون",
            config = AvatarConfig(
                presetId = "mig_cyber_techwear",
                customImageRes = R.drawable.img_avatar_cyber_techwear,
                background = "bg_nightcity",
                clothes = "cloth_cyber_techwear_violet",
                hair = "hair_spiky_brown",
                accessory = "acc_cyber_headphones",
                pet = "pet_shiba_inu"
            )
        ),
        MigPresetAvatar(
            id = "mig_denim_roses",
            name = "Blue Denim & Roses",
            nameAr = "جاكيت جينز وورد جوري وقطة",
            drawableRes = R.drawable.img_mig_avatar_denim_roses,
            badgeLabel = "Classic mig33",
            description = "أفاتار ميج الكلاسيكي: جاكيت جينز مع نظارة وباقة ورد وقطة وسور الحديقة",
            config = AvatarConfig(
                presetId = "mig_denim_roses",
                customImageRes = R.drawable.img_mig_avatar_denim_roses,
                background = "bg_garden",
                clothes = "cloth_denim_roses",
                hair = "hair_spiky_brown",
                accessory = "acc_blue_aviators",
                pet = "pet_shiba_inu"
            )
        ),
        MigPresetAvatar(
            id = "mig_leopard_chic",
            name = "Glam Leopard VIP",
            nameAr = "فاشن ليوپارد VIP",
            drawableRes = R.drawable.img_mig_avatar_leopard_chic,
            badgeLabel = "VIP Fashion",
            description = "فتاة الأناقة: فراء وتنورة نمر وجوارب نجوم وبوت وباقة إكسسوارات صالون VIP",
            config = AvatarConfig(
                presetId = "mig_leopard_chic",
                customImageRes = R.drawable.img_mig_avatar_leopard_chic,
                background = "bg_fashion",
                clothes = "cloth_leopard_chic",
                hair = "hair_wavy_pompadour_black",
                accessory = "acc_heart_bag_red",
                pet = "none"
            )
        ),
        MigPresetAvatar(
            id = "mig_rocker_star",
            name = "Rockstar Blaze",
            nameAr = "روك ستار الجيتار الناري",
            drawableRes = R.drawable.img_mig_avatar_rocker_star,
            badgeLabel = "Rock Legend",
            description = "روك ستار الحفلات: شعر ناري وجاكيت جلدي وجيتار Flying-V على مسرح الصخور",
            config = AvatarConfig(
                presetId = "mig_rocker_star",
                customImageRes = R.drawable.img_mig_avatar_rocker_star,
                background = "bg_concert",
                clothes = "cloth_rocker_leather",
                hair = "hair_pompadour_orange",
                accessory = "acc_flying_v_guitar",
                pet = "none"
            )
        ),
        MigPresetAvatar(
            id = "mig_pink_coquette",
            name = "Pink Coquette Princess",
            nameAr = "أميرة الكوكيت والتنين",
            drawableRes = R.drawable.img_mig_avatar_pink_coquette,
            badgeLabel = "Coquette Chic",
            description = "أميرة الكوكيت: فستان دانتيل وكورسيه مع قرون هاراجوكو وتنين أرجواني سحري",
            config = AvatarConfig(
                presetId = "mig_pink_coquette",
                customImageRes = R.drawable.img_mig_avatar_pink_coquette,
                background = "bg_abyssal_scene",
                clothes = "cloth_pink_coquette_gown",
                hair = "hair_harajuku_horns",
                accessory = "acc_tiara_royal",
                pet = "pet_crystal_dragon"
            )
        ),
        MigPresetAvatar(
            id = "mig_street_kitty",
            name = "Cyber Street Kitty",
            nameAr = "ستريت كيتي وسايبر",
            drawableRes = R.drawable.img_mig_avatar_street_kitty,
            badgeLabel = "Cyber Street",
            description = "هودي آذان القطة وسماعات سايبر نيون وجرو شيبا إينو في شوارع المدينة الليلية",
            config = AvatarConfig(
                presetId = "mig_street_kitty",
                customImageRes = R.drawable.img_mig_avatar_street_kitty,
                background = "bg_nightcity",
                clothes = "cloth_hikaru_kitty_hoodie",
                hair = "hair_harajuku_horns",
                accessory = "acc_cyber_headphones",
                pet = "pet_shiba_inu"
            )
        ),
        MigPresetAvatar(
            id = "mig_hipst4r_classic",
            name = "Classic Hipst4r",
            nameAr = "أفاتار هيبستر الأصلي",
            drawableRes = R.drawable.avatar_hipst4r,
            badgeLabel = "OG 2008",
            description = "أفاتار mig33 الأصلي لعام 2008",
            config = AvatarConfig(
                presetId = "mig_hipst4r_classic",
                customImageRes = R.drawable.avatar_hipst4r
            )
        ),
        MigPresetAvatar(
            id = "mig_b4sejump_retro",
            name = "BaseJumper",
            nameAr = "بيس جمبر الرياضي",
            drawableRes = R.drawable.avatar_b4sejump,
            badgeLabel = "Classic",
            description = "أفاتار mig33 الرياضي الكلاسيكي",
            config = AvatarConfig(
                presetId = "mig_b4sejump_retro",
                customImageRes = R.drawable.avatar_b4sejump
            )
        ),
        MigPresetAvatar(
            id = "mig_flyingkit_retro",
            name = "Flying Kit",
            nameAr = "فلايينغ كيت",
            drawableRes = R.drawable.avatar_f1yingkit,
            badgeLabel = "Classic",
            description = "أفاتار ميج الكلاسيكي الطائر",
            config = AvatarConfig(
                presetId = "mig_flyingkit_retro",
                customImageRes = R.drawable.avatar_f1yingkit
            )
        )
    )
}

data class MigPresetAvatar(
    val id: String,
    val name: String,
    val nameAr: String,
    val drawableRes: Int,
    val badgeLabel: String,
    val description: String,
    val config: AvatarConfig
)
