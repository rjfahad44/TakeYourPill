
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.jfrog.bintray")
}

android {
    namespace = "com.ft.ltd.takeyourpill"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ft.ltd.takeyourpill"
        minSdk = 24
        targetSdk = 34
        versionCode = 4
        versionName = "v1.1"

        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.incremental" to "true")
            }
        }
    }


    buildTypes {
        release {
            resValue("string", "admob_ads_id", "ca-app-pub-1858354192090396~4641049638")
            resValue("string", "admob_banner_ads_id", "ca-app-pub-1858354192090396/1168708485")
            resValue("string", "admob_interstitial_ads_id", "ca-app-pub-1858354192090396/7513242092")
            resValue("string", "admob_video_ads_id", "")
            resValue("string", "admob_open_ads_id", "ca-app-pub-1858354192090396/1372760605")
            resValue("string", "admob_native_ads_id", "ca-app-pub-1858354192090396/7756784673")
            resValue("string", "admob_reward_ads_id", "ca-app-pub-1858354192090396/8676535559")

            resValue("string", "facebook_banner_ads_id", "")
            resValue("string", "facebook_interstitial_ads_id", "")
            resValue("string", "facebook_native_ads_id", "")
            resValue("string", "facebook_video_native_ads_id", "")

            resValue("string", "max_interstitial", "0e403e4c05a15cff")
            resValue("string", "max_banner", "917f542c62884a61")
            resValue("string", "max_native", "1347ef7ac29302b8")

            isDebuggable  = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            resValue("string", "admob_ads_id", "ca-app-pub-7283749387189654~7391580023")
            resValue("string", "admob_banner_ads_id", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "admob_interstitial_ads_id", "ca-app-pub-3940256099942544/1033173712")
            resValue("string", "admob_video_ads_id", "")
            resValue("string", "admob_open_ads_id", "ca-app-pub-3940256099942544/3419835294")
            resValue("string", "admob_native_ads_id", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "admob_reward_ads_id", "ca-app-pub-3940256099942544/5224354917")

            resValue("string", "facebook_banner_ads_id", "")
            resValue("string", "facebook_interstitial_ads_id", "")
            resValue("string", "facebook_native_ads_id", "")
            resValue("string", "facebook_video_native_ads_id", "")

            resValue("string", "max_interstitial", "0e403e4c05a15cff")
            resValue("string", "max_banner", "917f542c62884a61")
            resValue("string", "max_native", "1347ef7ac29302b8")

            isDebuggable  = true
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    kapt {
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    bundle {
        storeArchive {
            enable = false
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-process:2.6.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    /*--------------------------------------------------------------------------------------------*/


    // In App Update
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")


    // viewModel() And activityViewModel()
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.viewpager2:viewpager2:1.0.0")


    //kotlin version
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")


    // Navigation components
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")

    // Room database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    // LifeCycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.6.2")

    // Permissions
    implementation("pub.devrel:easypermissions:3.0.0")

    // Number picker
    implementation("io.github.ShawnLin013:number-picker:2.4.13")

    // Material intro
    implementation("com.heinrichreimersoftware:material-intro:2.0.0")

    // Timber logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    //charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Custom image picker
    implementation(project(":imagepicker"))

    // ViewBinding delegate
    implementation("com.github.Zhuinden:fragmentviewbindingdelegate-kt:1.0.0")

    // Recycler loading skeletons
    implementation("com.faltenreich:skeletonlayout:4.0.0")

    //gson
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.10")

    //Ads service
    implementation("com.google.android.gms:play-services-ads:22.5.0")
    implementation("com.google.ads.interactivemedia.v3:interactivemedia:3.31.0")

    //Ads applovin
    implementation("com.google.ads.mediation:applovin:11.11.3.0")
    implementation("com.applovin:applovin-sdk:11.11.3")

    //Ads facebook
    implementation("com.applovin.mediation:facebook-adapter:6.8.0.9")
    implementation("com.facebook.android:facebook-android-sdk:15.2.0")

    //Ads google
    implementation("com.applovin.mediation:google-adapter:20.2.0.3")
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    implementation("com.google.android.gms:play-services-ads-lite:22.5.0")
    implementation("com.google.ads.interactivemedia.v3:interactivemedia:3.31.0")

    //firebase crashlytics
    implementation("com.google.firebase:firebase-crashlytics-ndk:18.6.0")
    implementation("com.google.firebase:firebase-analytics:21.5.0")

}