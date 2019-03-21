import React, { Component } from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import FileForm from './FileForm';
import PagesForm from './PagesForm';
import App from './App';
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faIgloo } from '@fortawesome/free-solid-svg-icons'

library.add(faIgloo)

class Home extends Component {
    render() {
        return (
            <Router>
                <Switch>
                    <Route path='/' exact={true} component={FileForm} />
                    <Route path='/calibrarPaginas/:name' exact={true} component={PagesForm} />
                    <Route path='/verResultados/:name' exact={true} component={App} />
                </Switch>
            </Router>
        );
    }
}

export default Home;
