// @flow

import React from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faTimes } from '@fortawesome/free-solid-svg-icons'
import { faCheck } from '@fortawesome/free-solid-svg-icons'

import type { T_Highlight } from "../../src/types";
type T_ManuscriptHighlight = T_Highlight;

type Props = {
  highlights: Array<T_ManuscriptHighlight>,
  basicFormatReport: Array<>,
  resetHighlights: () => void
};

const updateHash = highlight => {
  location.hash = `highlight-${highlight.id}`;
};

function Sidebar({ highlights, resetHighlights, basicFormatReport }: Props) {
  return (
    <div className="sidebar" style={{ width: "25vw" }}>
      <div className="description" style={{ padding: "1rem" }}>
        <h2 style={{ marginBottom: "1rem" }}>Reporte general</h2>

        <p style={{ fontSize: "0.7rem" }}>
          <a href="/">
            Volver al Inicio
          </a>
        </p>
      </div>
      <hr></hr>
      <div style={{ padding: "1rem" }}>
        <h4 style={{ marginBottom: "1rem" }} > Aspetos formales</h4>
        <table className="table table-bordered">
          <thead>
            <tr>
              <th scope="col">Formato básico</th>
              <th scope="col">Cumple</th>
            </tr>
          </thead>
          <tbody>
            {basicFormatReport.map((basicFormat, index) => (
              <tr
                key={index}
              >
                <td>{basicFormat.format}</td>
                {basicFormat.correct == true ? (
                  <td style={{ color: "green" }}> <FontAwesomeIcon icon={faCheck} /></td>
                ) : (<td style={{ color: "red" }}> <FontAwesomeIcon icon={faTimes} /> </td>)}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <hr></hr>
      <h4 style={{ marginBottom: "1rem", padding: "1rem" }} > Lista de errores de formato</h4>
      <ul className="sidebar__highlights">
        {highlights.map((highlight, index) => (
          <li
            key={index}
            className="sidebar__highlight"
            onClick={() => {
              updateHash(highlight);
            }}
          >
            <div>
              <p><strong>Por favor verifique: </strong>{highlight.comment.text}</p>
              {highlight.content.text ? (
                <blockquote style={{ marginTop: "0.5rem" }}>
                  {`${highlight.content.text.slice(0, 90).trim()}…`}
                </blockquote>
              ) : null}
              {highlight.content.image ? (
                <div
                  className="highlight__image"
                  style={{ marginTop: "0.5rem" }}
                >
                  <img src={highlight.content.image} alt={"Screenshot"} />
                </div>
              ) : null}
            </div>
            <div className="highlight__location">
              Página {highlight.position.pageNumber}
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Sidebar;
