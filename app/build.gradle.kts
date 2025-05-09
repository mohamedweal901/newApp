plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.google.ksp)
    alias(libs.plugins.androidx.navigation.safeargs)
}

android {
    buildFeatures {
        viewBinding = true
    }
    composeOptions {
        namespace = "com.example.thenewsapp"
        compileSdk = 35

        defaultConfig {
            applicationId = "com.example.thenewsapp"
            minSdk = 24
            targetSdk = 34
            versionCode = 1
            versionName = "1.0"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildTypes {
            release {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    dependencies {

        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)



        //coroutines
        implementation(libs.kotlinx.coroutines.android)
        implementation(libs.androidx.room.ktx)

        // Room
        implementation(libs.androidx.room.runtime)
        implementation(libs.androidx.room.ktx)
        ksp(libs.androidx.room.compiler)

        // Navigation
        implementation(libs.androidx.navigation.fragment.ktx)
        implementation(libs.androidx.navigation.ui.ktx)


        // lifecycle (viewmodel)
        implementation(libs.androidx.lifecycle.viewmodel.ktx)
        // lifecycle (livedata)
        implementation(libs.androidx.lifecycle.livedata.ktx)
        ksp(libs.androidx.lifecycle.compiler)

        //Glide
        implementation(libs.glide)
        ksp(libs.glide.compiler)

         //retrofit
        implementation(libs.retrofit)
        implementation(libs.retrofit.converter.gson)
        //interceptor
        implementation(libs.okhttp.logging.interceptor)






    }
}
dependencies {
    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
}
