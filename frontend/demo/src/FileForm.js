import React, { Component } from 'react';


class FileForm extends Component {

    constructor(props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.uploadSingleFile = this.uploadSingleFile.bind(this);
        this.fileInput = React.createRef();
        this.singleUploadForm = document.querySelector('#singleUploadForm');
        this.singleFileUploadInput = document.querySelector('#singleFileUploadInput');
        this.singleFileUploadError = document.querySelector('#singleFileUploadError');
        this.singleFileUploadSuccess = document.querySelector('#singleFileUploadSuccess');
    }

    handleSubmit(event) {
        event.preventDefault();
        var files = singleFileUploadInput.files;
        if (files.length === 0) {
            singleFileUploadError.innerHTML = "Por favor seleccione un archivo PDF.";
            singleFileUploadError.style.display = "block";
        }
        this.uploadSingleFile(files[0]);
    }

    uploadSingleFile(file) {
        var formData = new FormData();
        formData.append("file", file);
        fetch('/api/uploadFile', {
            method: 'POST',
            body: formData
        }).then(
            response => {
                return response.json();
            }
        ).then(
            data => {
                console.log(data);
                if (data.status) {
                    throw new Error((data && data.message) || "Ocurrió algún error.");
                } else {
                    singleFileUploadError.style.display = "none";
                    singleFileUploadSuccess.innerHTML = "<p> El archivo fue subido correctamente. </p> <p> Por favor espere un momento</p>";
                    singleFileUploadSuccess.style.display = "block";
                    setTimeout(function () { this.props.history.push(`/calibrarPaginas/${encodeURI(file.name)}`) }.bind(this), 2000);
                }

            }
        ).catch(
            error => {
                singleFileUploadSuccess.style.display = "none";
                singleFileUploadError.innerHTML = error.message;
            }
        );

    }

    render() {
        return (
            <div style={{ color: "black" }}>
                <center>
                    <h3> Sistema de ayuda para la detección y corrección de errores de formato en trabajos académicos</h3>
                </center>
                <form onSubmit={this.handleSubmit} id="singleUploadForm" name="singleUploadForm">
                    <label>
                        Suba su PDF:
                        <input id="singleFileUploadInput" type="file" name="file" className="file-input" accept="application/pdf" required ref={this.fileInput} />
                    </label>
                    <br />
                    <button type="submit">Enviar</button>
                </form>
                <div className="upload-response">
                    <div id="singleFileUploadError"></div>
                    <div id="singleFileUploadSuccess"></div>
                </div>
            </div>
        );
    }
}

export default FileForm;
