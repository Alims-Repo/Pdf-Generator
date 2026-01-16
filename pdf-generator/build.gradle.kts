import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.kotlin.dsl.support.kotlinCompilerOptions

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    id("com.vanniktech.maven.publish") version "0.28.0"
}

android {
    namespace = "io.github.alimsrepo.pdf.generator"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.zxing.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}


mavenPublishing {
    coordinates(
        groupId = "io.github.alims-repo",
        artifactId = "pdf-generator",
        version = "1.0.6-beta"
    )

    pom {
        name.set("Pdf-Generator")
        description.set("A lightweight Kotlin DSL for generating multi-page PDFs on Android with A4 support, tables, text, and automatic pagination.")
        inceptionYear.set("2026")
        url.set("https://github.com/Alims-Repo/Pdf-Generator")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("alim")
                name.set("Alim Sourav")
                email.set("sourav.0.alim@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/Alims-Repo/Pdf-Generator")
            connection.set("scm:git:git://github.com/Alims-Repo/Pdf-Generator.git")
            developerConnection.set("scm:git:ssh://github.com/Alims-Repo/Pdf-Generator.git")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
}