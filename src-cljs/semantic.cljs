(ns semantic
  (:refer-clojure :exclude [list])
  (:require ["semantic-ui-react" :as semantic]
            [reagent.core :as reagent]))

(def built-input semantic/Input)
(def built-button semantic/Button)

(def input (reagent/adapt-react-class built-input))
(def rating (reagent/adapt-react-class semantic/Rating))
(def image (reagent/adapt-react-class semantic/Image))
(def label (reagent/adapt-react-class semantic/Label))
(def sticky (reagent/adapt-react-class semantic/Sticky))
(def container (reagent/adapt-react-class semantic/Container))
(def divider (reagent/adapt-react-class semantic/Divider))
(def radio (reagent/adapt-react-class semantic/Radio))
(def checkbox (reagent/adapt-react-class semantic/Checkbox))
(def transition (reagent/adapt-react-class semantic/Transition))
(def dimmer (reagent/adapt-react-class semantic/Dimmer))
(def loader (reagent/adapt-react-class semantic/Loader))

(def statistic (reagent/adapt-react-class semantic/Statistic))
(def statistic-group (reagent/adapt-react-class semantic/Statistic.Group))
(def statistic-label (reagent/adapt-react-class semantic/Statistic.Label))
(def statistic-value (reagent/adapt-react-class semantic/Statistic.Value))

(def search (reagent/adapt-react-class semantic/Search))

(def icon (reagent/adapt-react-class semantic/Icon))
(def icon-group (reagent/adapt-react-class semantic/Icon.Group))

(def segment (reagent/adapt-react-class semantic/Segment))
(def segment-group (reagent/adapt-react-class semantic/Segment.Group))

(def grid (reagent/adapt-react-class semantic/Grid))
(def grid-column (reagent/adapt-react-class semantic/Grid.Column))
(def grid-row (reagent/adapt-react-class semantic/Grid.Row))

(def menu (reagent/adapt-react-class semantic/Menu))
(def menu-menu (reagent/adapt-react-class semantic/Menu.Menu))
(def menu-item (reagent/adapt-react-class semantic/Menu.Item))

(def list (reagent/adapt-react-class semantic/List))
(def list-item (reagent/adapt-react-class semantic/List.Item))

(def card-group (reagent/adapt-react-class semantic/Card.Group))
(def card (reagent/adapt-react-class semantic/Card))
(def card-content (reagent/adapt-react-class semantic/Card.Content))
(def card-header (reagent/adapt-react-class semantic/Card.Header))
(def card-meta (reagent/adapt-react-class semantic/Card.Meta))
(def card-description (reagent/adapt-react-class semantic/Card.Description))

(def dropdown (reagent/adapt-react-class semantic/Dropdown))
(def dropdown-divider (reagent/adapt-react-class semantic/Dropdown.Divider))
(def dropdown-header (reagent/adapt-react-class semantic/Dropdown.Header))
(def dropdown-item (reagent/adapt-react-class semantic/Dropdown.Item))
(def dropdown-menu (reagent/adapt-react-class semantic/Dropdown.Menu))
(def dropdown-search (reagent/adapt-react-class semantic/Dropdown.SearchInput))

(def message (reagent/adapt-react-class semantic/Message))
(def message-content (reagent/adapt-react-class semantic/Message.Content))
(def message-header (reagent/adapt-react-class semantic/Message.Header))
(def message-item (reagent/adapt-react-class semantic/Message.Item))
(def message-list (reagent/adapt-react-class semantic/Message.List))

(def header (reagent/adapt-react-class semantic/Header))
(def header-content (reagent/adapt-react-class semantic/Header.Content))
(def header-subheader (reagent/adapt-react-class semantic/Header.Subheader))

(def form (reagent/adapt-react-class semantic/Form))
(def form-button (reagent/adapt-react-class semantic/Form.Button))
(def form-checkbox (reagent/adapt-react-class semantic/Form.Checkbox))
(def form-dropdown (reagent/adapt-react-class semantic/Form.Dropdown))
(def form-field (reagent/adapt-react-class semantic/Form.Field))
(def form-group (reagent/adapt-react-class semantic/Form.Group))
(def form-input (reagent/adapt-react-class semantic/Form.Input))
(def form-radio (reagent/adapt-react-class semantic/Form.Radio))
(def form-select (reagent/adapt-react-class semantic/Form.Select))
(def form-textarea (reagent/adapt-react-class semantic/Form.TextArea))

(def popup (reagent/adapt-react-class semantic/Popup))
(def popup-content (reagent/adapt-react-class semantic/Popup.Content))
(def popup-header (reagent/adapt-react-class semantic/Popup.Header))

(def table (reagent/adapt-react-class semantic/Table))
(def table-body (reagent/adapt-react-class semantic/Table.Body))
(def table-cell (reagent/adapt-react-class semantic/Table.Cell))
(def table-footer (reagent/adapt-react-class semantic/Table.Footer))
(def table-header (reagent/adapt-react-class semantic/Table.Header))
(def table-header-cell (reagent/adapt-react-class semantic/Table.HeaderCell))
(def table-row (reagent/adapt-react-class semantic/Table.Row))

(def button (reagent/adapt-react-class built-button))
(def button-content (reagent/adapt-react-class semantic/Button.Content))
(def button-group (reagent/adapt-react-class semantic/Button.Group))
(def button-or (reagent/adapt-react-class semantic/Button.Or))

(def textarea (reagent/adapt-react-class semantic/TextArea))

(def placeholder (reagent/adapt-react-class semantic/Placeholder))
(def placeholder-paragraph (reagent/adapt-react-class semantic/Placeholder.Paragraph))
(def placeholder-line (reagent/adapt-react-class semantic/Placeholder.Line))
(def placeholder-image (reagent/adapt-react-class semantic/Placeholder.Image))

(def modal (reagent/adapt-react-class semantic/Modal))
(def modal-header (reagent/adapt-react-class semantic/Modal.Header))
(def modal-content (reagent/adapt-react-class semantic/Modal.Content))

(def progress (reagent/adapt-react-class semantic/Progress))

(defn tab [items]
  (let [tab* (reagent/adapt-react-class semantic/Tab)]
    [tab* {:panes (for [[pane opts & body] items]
                    {:menuItem (:menuItem opts)
                     :render   (fn []
                                 (reagent/as-element
                                  (into [pane (dissoc opts :menuItem)] body)))})}]))

(def tab-pane (reagent/adapt-react-class semantic/Tab.Pane))