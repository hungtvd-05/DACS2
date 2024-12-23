document.getElementById("sale").onchange = function() {giasale()};
document.getElementById("gia").onchange = function() {giasale()};

function giasale() {
    var sale = document.getElementById("sale").value;
    var gia = document.getElementById("gia").value;
    var giasale = document.getElementById("giasale");

    giasale.value = gia * (100 - sale) / 100;
}

let imageNames = [];
Dropzone.autoDiscover = false;
const dropzone = new Dropzone("#imageUpload", {
    url: "/admin/luu-anh",
    maxFiles: 8,
    paramName: 'file1',
    acceptedFiles: "image/*, .webp",
    addRemoveLinks: true,
    success: function(file, response) {
        imageNames.push(file.name);
    },
    error: function(file, response) {
        console.error(response);
    },
    removedfile: function(file) {
        const fileName = file.name;
        const index = imageNames.indexOf(fileName);
        imageNames.splice(index, 1);

        // Tạo FormData object
        const formData = new FormData();
        formData.append('fileName', fileName);

        // Gọi API xóa file
        fetch('/admin/xoa-anh', {
            method: 'POST',
            body: formData
        })

        // Xóa preview
        if (file.previewElement != null && file.previewElement.parentNode != null) {
            file.previewElement.parentNode.removeChild(file.previewElement);
        }

        return this._updateMaxFilesReachedClass();
    }
});


document.getElementById('productForm').addEventListener('submit', function(e) {
    e.preventDefault();
    // Tạo input hidden để chứa danh sách tên file
    const imageNamesInput = document.createElement('input');
    imageNamesInput.type = 'hidden';
    imageNamesInput.name = 'imageNames';

    imageNamesInput.value = JSON.stringify(imageNames.toString());

    // Thêm input vào form
    this.appendChild(imageNamesInput);

    // Submit form
    this.submit();
});