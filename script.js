
//Heaser and footer

class SpecialHeader extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <header class="hero-other-pages">
        <div class="navbar-other-pages">
            <div class="logo">
                <img src="img/logo_oria.png" alt="Logo Oria" width="100">
            </div>
            <div class="bar-links">
                <a href="index.html">Accueil</a>
                <a href="#Histoire">Histoire</a>
                <a href="#Equipe">Equipe</a>
                <a href="Projets.html">Projets</a>
                <a href="Services.html">Services</a>
            </div>
            <button class="hero-btn">
                <label><a href="mailto:agency.oria@gmail.com">Contactez-nous</a></label>
            </button>
        </div>
        </header>
  `;
    }
}

class SpecialFooter extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <footer class="footer">
            <div class="footer-container">
                <div class="footer-logo">
                    <img src="img/logo_oria.png" alt="Logo ORIA" width="120">
                </div>
                <div class="footer-columns">
                    <!-- Colonne 1 : Réseaux sociaux -->
                    <div class="footer-column">
                        <h4>Suivez-nous</h4>
                        <div class="social-icons">
                            <a href="#"><i class="fa fa-facebook"></i></a>
                            <a href="#"><i class="fa fa-instagram"></i></a>
                            <a href="#"><i class="fa fa-tiktok"></i></a>
                            <a href="#"><i class="fa fa-linkedin"></i></a>
                        </div>
                    </div>
                    <!-- Colonne 2 : Nos Services -->
                    <div class="footer-column">
                        <h4>Nos Services</h4>
                        <p>Marquages publicitaires</p>
                        <p>Services web</p>
                        <p>Graphisme</p>
                        <p>Impression</p>
                    </div>
                    <!-- Colonne 3 : Nous contacter -->
                    <div class="footer-column">
                        <h4>Nous contacter</h4>
                        <p><i class="fa fa-envelope"></i> agency.oria@gmail.com</p>
                        <p><i class="fa fa-phone"></i> 04 06 08 77 42</p>
                        <p><i class="fa fa-map-marker"></i> 1 Rue de Chablis, 93000 Bobigny</p>
                    </div>
                </div>
                <hr>
                <div class="footer-bottom">
                    <p>
                        Politique de confidentialité | Cookies | Mentions légales | Aide / FAQ / Contact
                    </p>
                </div>
            </div>
        </footer>
        `;
    }
}

// Enregistrer le composant personnalisé
customElements.define('special-header', SpecialHeader);

customElements.define('special-footer', SpecialFooter);


//Le carousel


const track = document.querySelector('.carousel-track');
const slides = Array.from(track.children);
const nextButton = document.querySelector('.carousel-btn.next');
const prevButton = document.querySelector('.carousel-btn.prev');

let currentIndex = 0;

function updateSlides() {
    track.style.transform = `translateX(-${currentIndex * 100}%)`;
}

// Next Button functionality
nextButton.addEventListener('click', () => {
    currentIndex = (currentIndex + 1) % slides.length;
    updateSlides();
});

// Previous Button functionality
prevButton.addEventListener('click', () => {
    currentIndex = (currentIndex - 1 + slides.length) % slides.length;
    updateSlides();
});

// Initial Setup
updateSlides();
