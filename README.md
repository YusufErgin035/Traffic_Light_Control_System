# Traffic Light Control System

Bu proje, JavaFX kullanılarak geliştirilmiş akıllı bir trafik ışığı kontrol sistemi simülasyonudur. Sistem, gerçek zamanlı trafik yoğunluğuna göre dinamik olarak trafik ışıklarını kontrol eder.

## 🚦 Özellikler

- **Akıllı Trafik Kontrolü**: Sistem, her kavşaktaki araç yoğunluğunu analiz ederek en yoğun yöne yeşil ışık verir
- **Gerçek Zamanlı Simülasyon**: Araçlar rastgele başlangıç ve bitiş noktaları ile otomatik olarak oluşturulur
- **Dijkstra Algoritması**: En kısa yol bulma için Dijkstra algoritması kullanılır
- **Animasyonlu Araç Hareketi**: Araçlar gerçekçi yol takibi ve dönüş animasyonları ile hareket eder
- **Çoklu Kavşak Sistemi**: 4 farklı kavşağın eş zamanlı kontrolü

## 🏗️ Sistem Mimarisi

### Ana Bileşenler

1. **Graph.java**: Yol ağını temsil eden graf yapısı
2. **TrafficLightSystem.java**: Trafik ışıklarının akıllı kontrolü
3. **Car.java**: Araç nesnesi ve hareket animasyonları
4. **DestinationMaker.java**: Araç rotalarının oluşturulması ve yönetimi
5. **Edge.java**: Yol segmentlerini temsil eden kenar sınıfı
6. **TrafficController.java**: Ana UI kontrolcüsü

### Kavşak Sistemi

Sistem 4 ana kavşağa sahiptir:
- **Sol Üst (LU)**: Node 8
- **Sağ Üst (RU)**: Node 9
- **Sol Alt (LD)**: Node 10
- **Sağ Alt (RD)**: Node 11

Her kavşakta 4 yönden gelen trafik için ayrı ışıklar bulunur.

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler

- Java 11 veya üzeri
- JavaFX 23.0.1
- Maven (bağımlılık yönetimi için)

### Kurulum Adımları

1. Projeyi klonlayın:
```bash
git clone [repository-url]
cd traffic-light-control-system
```

2. Maven ile derleyin:
```bash
mvn clean compile
```

3. Uygulamayı çalıştırın:
```bash
mvn javafx:run
```

### Manuel Çalıştırma

```bash
java -cp target/classes --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml com.example.trafficlightcontrolsystem.MainApplication
```

## 🎮 Kullanım

1. **Ana Ekran**: Uygulamayı başlattığınızda "This is Life" yazılı ana ekran görünür
2. **Simülasyon**: Ana ekrana tıklayarak trafik simülasyonu ekranına geçin
3. **Otomatik Çalışma**: Sistem otomatik olarak:
   - 4 saniye sonra araç üretmeye başlar
   - Her 2.5 saniyede bir yeni araç oluşturur
   - Her 5 saniyede trafik ışıklarını günceller
4. **Geri Dönüş**: "Go Back" butonu ile ana ekrana dönebilirsiniz

## 🧠 Algoritma Detayları

### Trafik Işığı Algoritması

```java
// Her 5 saniyede bir çalışır
1. Tüm ışıkları kırmızıya çevir
2. Her kavşak için:
   - En yoğun gelen yönü belirle
   - O yöne yeşil ışık ver
3. Eğer hiç araç yoksa rastgele bir yön seç
```

### Araç Yol Bulma

1. **Dijkstra Algoritması**: En kısa yolu bulur
2. **Edge Weight**: Mevcut araç sayısına göre hesaplanır
3. **Dynamic Routing**: Trafik yoğunluğuna göre rota optimize edilir

### Araç Hareketi

- **Path Animation**: Araçlar belirlenen yol üzerinde hareket eder
- **Turn Detection**: Dönüş gereken yerler otomatik tespit edilir
- **Traffic Light Respect**: Araçlar kırmızı ışıkta bekler

## 📁 Dosya Yapısı

```
src/main/java/com/example/trafficlightcontrolsystem/
├── MainApplication.java          # Ana uygulama sınıfı
├── MainController.java           # Ana ekran kontrolcüsü
├── TrafficController.java        # Trafik simülasyonu kontrolcüsü
├── graph.java                    # Graf yapısı ve Dijkstra algoritması
├── Edge.java                     # Yol segmenti sınıfı
├── Car.java                      # Araç sınıfı ve animasyonları
├── DestinationMaker.java         # Araç rotası yöneticisi
└── TrafficLightSystem.java       # Trafik ışığı kontrol sistemi

src/main/resources/com/example/trafficlightcontrolsystem/
├── anasayfa.fxml                 # Ana ekran UI
└── traffic.fxml                  # Trafik simülasyonu UI
```

## 🎯 Teknik Özellikler

- **Multithreaded Animation**: Birden fazla araç eş zamanlı hareket eder
- **Memory Management**: Araçlar simülasyondan çıktığında otomatik temizlenir
- **Real-time Graph Updates**: Graf yapısı gerçek zamanlı güncellenir
- **Collision Avoidance**: Aynı yolda araç sayısı sınırlandırılır

## 🐛 Bilinen Sorunlar

- Çok yoğun trafik durumunda bazı araçlar uzun süre bekleyebilir
- Sistem kapatılırken animasyonların tamamen durması birkaç saniye alabilir

## 🤝 Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/AmazingFeature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add some AmazingFeature'`)
4. Branch'inizi push edin (`git push origin feature/AmazingFeature`)
5. Pull Request oluşturun

## 📄 Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için `LICENSE` dosyasına bakınız.

## 👨‍💻 Geliştiriciler

- Proje geliştiricisi: [Your Name]
- İletişim: [your-email@example.com]

## 📚 Referanslar

- [JavaFX Documentation](https://openjfx.io/)
- [Dijkstra Algorithm](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm)
- [Graph Theory](https://en.wikipedia.org/wiki/Graph_theory)