package com.example.pan.data.model

data class Course(
    val id: String,
    val title: String,
    val semester: String,
    val ects: Int,
    val professor: String,
    val description: String
)

val mockCourses: List<Course> = listOf(
    Course(
        id          = "cs101",
        title       = "Εισαγωγή στον Προγραμματισμό",
        semester    = "Α' Εξάμηνο",
        ects        = 6,
        professor   = "Γ. Παπαδόπουλος",
        description = "Βασικές αρχές αλγοριθμικής σκέψης και δομημένου προγραμματισμού " +
                      "με Python. Καλύπτει μεταβλητές, εντολές ελέγχου ροής, βρόχους, " +
                      "συναρτήσεις και εισαγωγή στον αντικειμενοστρεφή προγραμματισμό."
    ),
    Course(
        id          = "cs102",
        title       = "Διακριτά Μαθηματικά",
        semester    = "Α' Εξάμηνο",
        ects        = 6,
        professor   = "Μ. Αλεξίου",
        description = "Λογική πρότασης και κατηγορημάτων, σύνολα, σχέσεις και συναρτήσεις, " +
                      "γραφήματα και θεωρία αριθμών. Θεμέλια για αλγορίθμους και θεωρητική " +
                      "πληροφορική."
    ),
    Course(
        id          = "cs201",
        title       = "Δομές Δεδομένων",
        semester    = "Β' Εξάμηνο",
        ects        = 6,
        professor   = "Κ. Νικολόπουλος",
        description = "Στοίβες, ουρές, συνδεδεμένες λίστες, δέντρα (AVL, B-trees) και " +
                      "πίνακες κατακερματισμού. Ανάλυση χρόνου και χώρου εκτέλεσης, " +
                      "υλοποίηση αλγορίθμων σε Java."
    ),
    Course(
        id          = "cs202",
        title       = "Αντικειμενοστρεφής Προγραμματισμός",
        semester    = "Β' Εξάμηνο",
        ects        = 6,
        professor   = "Σ. Λυκοθανάσης",
        description = "Κλάσεις, κληρονομικότητα, πολυμορφισμός και ενθυλάκωση. Εισαγωγή " +
                      "σε design patterns (Factory, Singleton, Observer). Ανάπτυξη " +
                      "εφαρμογών με Java."
    ),
    Course(
        id          = "cs301",
        title       = "Αλγόριθμοι και Πολυπλοκότητα",
        semester    = "Γ' Εξάμηνο",
        ects        = 6,
        professor   = "Β. Μάμαλης",
        description = "Σχεδιασμός αλγορίθμων: διαίρει και βασίλευε, δυναμικός προγραμματισμός, " +
                      "greedy. Κλάσεις P και NP, αναγωγές, NP-πληρότητα. " +
                      "Εισαγωγή σε προσεγγιστικούς αλγορίθμους."
    ),
)
