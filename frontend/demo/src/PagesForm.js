import React, { Component } from 'react';


class PagesForm extends Component {

    constructor(props) {
        super(props);
        this.state = {
            coverPage: '',
            generalIndexPageStart: '',
            generalIndexPageEnd: '',
            figureTableIndexPageEnd: '',
            biographyPage: '',
            annexedPage: '',
            isLoading: true,
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    async componentDidMount() {
        var pages = await (await fetch(`/api/getPages/${encodeURI(this.props.match.params.name)}`)).json();
        console.log(pages);
        console.log(pages.lenght);
        console.log(pages[6]);
        this.setState({ coverPage: pages[0], generalIndexPageStart: pages[1], generalIndexPageEnd: pages[2], figureTableIndexPageEnd: pages[3], biographyPage: pages[4], annexedPage: pages[5] });
        this.setState({ isLoading: false });
    }

    handleChange(event) {
        this.setState({ [event.target.name]: event.target.value });
    }


    handleSubmit(event) {
        event.preventDefault();
        const searchParams = new URLSearchParams();
        searchParams.set("coverPage", this.state.coverPage);
        searchParams.set("generalIndexPageStart", this.state.generalIndexPageStart);
        searchParams.set("generalIndexPageEnd", this.state.generalIndexPageEnd);
        searchParams.set("figureTableIndexPageEnd", this.state.figureTableIndexPageEnd);
        searchParams.set("biographyPage", this.state.biographyPage);
        searchParams.set("annexedPage", this.state.annexedPage);
        const url = searchParams.toString();
        setTimeout(function () { this.props.history.push(`/verResultados/${encodeURI(this.props.match.params.name)}` + `?${url}`) }.bind(this), 2000);

    }



    render() {
        if (this.state.isLoading) {
            return <p style={{ color: "black" }}>Cargando...</p>;
        }
        return (
            <div style={{ color: "black" }}>
                <center>
                    <h3> Sistema de ayuda para la detección y corrección de errores de formato en trabajos académicos</h3>
                </center>
                <form onSubmit={this.handleSubmit} id="pagesForm" name="pagesForm">
                    <label>
                        Página de la carátula:
                    </label>
                    <input type="text" name="coverPage" pattern="[0-9]*" value={this.state.coverPage} onChange={this.handleChange} />
                    <br></br>

                    <label>
                        Página inicio del índice general:
                    </label>
                    <input type="text" name="generalIndexPageStart" pattern="[0-9]*" value={this.state.generalIndexPageStart} onChange={this.handleChange} />
                    <br></br>

                    <label>
                        Página fin del índice general:
                    </label>
                    <input type="text" name="generalIndexPageEnd" pattern="[0-9]*" value={this.state.generalIndexPageEnd} onChange={this.handleChange} />
                    <br></br>

                    <label>
                        Página fin del índice de tablas y figuras:
                    </label>
                    <input type="text" name="figureTableIndexPageEnd" pattern="[0-9]*" value={this.state.figureTableIndexPageEnd} onChange={this.handleChange} />
                    <br></br>

                    <label>
                        Página inicio de la bibliografía:
                    </label>
                    <input type="text" name="biographyPage" pattern="[0-9]*" value={this.state.biographyPage} onChange={this.handleChange} />
                    <br></br>

                    <label>
                        Página inicio de los anexos:
                    </label>
                    <input type="text" name="annexedPage" pattern="[0-9]*" value={this.state.annexedPage} onChange={this.handleChange} />
                    <br></br>

                    <button type="submit">Enviar</button>
                </form>
            </div>
        );
    }
}

export default PagesForm;
