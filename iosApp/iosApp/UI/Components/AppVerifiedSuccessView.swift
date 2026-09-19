import SwiftUI

/// Layar "Blocking" (verifikasi/hasil) sesuai desain Figma — ilustrasi full-bleed 360pt,
/// tombol close (X) di pojok kiri atas, title + body, satu tombol merah penuh di bawah.
/// Dipakai untuk hasil login/register.
///
/// Warna & tipe diambil literal dari token desain Figma (bukan warna sistem) supaya
/// screen ini cocok pixel-untuk-pixel dengan referensinya.
struct AppVerifiedSuccessView: View {
    let title: String
    let message: String
    let buttonTitle: String
    let onClose: () -> Void

    var body: some View {
        ZStack(alignment: .topLeading) {
            VStack(spacing: 0) {
                Image("img_verified_success")
                    .resizable()
                    .scaledToFill()
                    .frame(maxWidth: .infinity)
                    .frame(height: 360)
                    .clipped()
                    .ignoresSafeArea(edges: .top)

                Spacer().frame(height: 21)
                AppBlockingTitle(text: title)
                Spacer().frame(height: 5)
                AppBlockingMessage(text: message)
                Spacer()
                AppBlockingPrimaryButton(title: buttonTitle, action: onClose)
            }
            .frame(maxWidth: .infinity)

            AppBlockingCloseButton(action: onClose)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.white.ignoresSafeArea())
    }
}
