
import com.mayurg.buildsrc.AndroidX
import com.mayurg.buildsrc.Location
import com.mayurg.buildsrc.Logging
import com.mayurg.buildsrc.Retrofit
import com.mayurg.buildsrc.Testing
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.vanniktech.maven.publish") version "0.28.0"
}

mavenPublishing {
    configure(
        AndroidSingleVariantLibrary(
        // the published variant
        variant = "release",
        // whether to publish a sources jar
        sourcesJar = true,
        // whether to publish a javadoc jar
        publishJavadocJar = true,
    )
    )

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates("com.mayurgajra",version = "1.0.0")

    pom {
        name.set("Bird Location SDK")
        description.set("Example of location sdk")
        inceptionYear.set("2024")
        url.set("https://github.com/mayurgajra/BirdLocationSDK")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("mayurgajra")
                name.set("Mayur Gajra")
                url.set("https://github.com/mayurgajra/")
            }
        }
        scm {
            url.set("https://github.com/mayurgajra/BirdLocationSDK")
            connection.set("scm:git:git@github.com:mayurgajra/BirdLocationSDK.git")
            developerConnection.set("scm:git:ssh://git@github.com:mayurgajra/BirdLocationSDK.git")
        }
    }
}

android {
    namespace = "com.mayurg.locationsdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(Retrofit.okHttp)
    implementation(Retrofit.retrofit)
    implementation(Retrofit.okHttpLoggingInterceptor)
    implementation(Retrofit.moshiConverter)
    implementation(Retrofit.moshiAdapter)

    // Location Services
    implementation(Location.gmsLocation)
    // logging
    implementation(Logging.timber)
    // security
    implementation(AndroidX.security)

    testImplementation(libs.junit)
    testImplementation(Testing.mockitoCore)
    testImplementation(Testing.coroutinesTest)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(Testing.mockitoAndroid)
}