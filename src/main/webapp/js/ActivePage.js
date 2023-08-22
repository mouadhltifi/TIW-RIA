{
    var homePageContainer, searchElementContainer, itemDetailsContainer, cartPageContainer, ordersContainer
    pageOrchestrator = new PageOrchestrator();

    window.addEventListener("load", () => {
        if (sessionStorage.getItem("username") == null) {
            window.location.href = "index";
        } else {
            pageOrchestrator.start();
            pageOrchestrator.refresh();
            pageOrchestrator.goToHomePage();


        }
    }, false);

    function PersonalMessage(_username, messagecontainer) {
        this.username = _username;

        this.show = function () {
            messagecontainer.textContent = this.username;
        }
    }

    function HomePageContainer(_alertContainer, _homeViewContainer, _tableID, _id_searchItem, _pageOrchestrator, _lastSeenTableBody) {

        //linking html with DOM
        this.alert = _alertContainer;
        this.homeViewContainer = _homeViewContainer;
        this.tableLastSeenItem = _tableID;
        this.searchFrom = _id_searchItem;
        this.tableBody = _lastSeenTableBody;

        //for next step
        this.orchestrator = _pageOrchestrator;

        // this.reset = function () {
        //     this.homeViewContainer.style.visibility = "hidden";
        // }

        //showing component
        this.show = function () {
            var self = this;

            this.searchFrom.style.display = 'block';


            makeCall("GET", "GoToHome", null,
                function (req) {
                    if (req.readyState === 4) {
                        var message = req.responseText;
                        if (req.status === 200) {
                            var itemsList = JSON.parse(req.responseText);

                            self.update(Array.from(itemsList)); // self visible by closure

                            if (itemsList.length === 0) {
                                self.alert.textContent = "no items found";
                                self.alert.style.display = 'block';
                            }


                        } else if (req.status === 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem('username');
                        } else {
                            self.alert.textContent = message;
                        }
                    }
                });


        };

        //what component need to do
        this.update = function (itemList) {

            //this.tableLastSeenItem.innerHTML = "";
            var self = this;
            var elem, i, row, nameCell, descriptionCell, catgCell, linkcell, anchor, imgCell;

            this.tableBody.innerHTML = "";
            Array.from(itemList).forEach(function (item) {
                row = document.createElement("tr");

                nameCell = document.createElement("td");
                nameCell.textContent = item.name;
                row.appendChild(nameCell);

                descriptionCell = document.createElement("td");
                descriptionCell.textContent = item.description;
                row.appendChild(descriptionCell);

                catgCell = document.createElement("td");
                catgCell.textContent = item.category;
                row.appendChild(catgCell);

                linkcell = document.createElement("td");
                anchor = document.createElement("a");
                linkcell.appendChild(anchor);
                linkText = document.createTextNode("Show");
                anchor.appendChild(linkText);

                anchor.setAttribute('itemId', item.id_item); // set a custom HTML attribute

                anchor.addEventListener("click", (e) => {
                    // dependency via module parameter
                    self.orchestrator.showItemDetails(anchor.getAttribute("itemId"))
                    // searchedList.show(anchor.getAttribute("itemId")); // passing id for query
                }, false);
                anchor.href = "#";
                row.appendChild(linkcell);

                self.tableBody.appendChild(row);
            });

            this.homeViewContainer.style.display = 'block';


            this.searchFrom.querySelector('input[type="button"]').addEventListener('click',
                (e) => {

                    self.orchestrator.showSearchElement(e.target.closest("form"));

                    /*
                    var form = e.target.closest("form");
                    if (form.checkValidity()) {
                        makeCall("POST", 'searchItems', e.target.closest("form"),
                            function(x) {
                                if (x.readyState == XMLHttpRequest.DONE) {
                                    var message = x.responseText;
                                    switch (x.status) {
                                        case 200:
                                            pageOrchestrator.showSearchElement(Array.from(message));
                                            break;
                                        case 400: // bad request
                                            document.getElementById("errorMessage").textContent = message;
                                            break;
                                        case 401: // unauthorized
                                            document.getElementById("errorMessage").textContent = message;
                                            break;
                                        case 500: // server error
                                            document.getElementById("errorMessage").textContent = message;
                                            break;
                                    }
                                }
                            }
                        );
                    } else {
                        form.reportValidity();
                    }
                     */
                });
        }

        this.reset = function () {
            this.homeViewContainer.style.display = 'none';
            this.tableBody.innerHTML = "";
            this.searchFrom.style.display = 'none';

        }
    }


    function SearchElementContainer(_alertContainer, _searchContainer, _table, _noItemFound, _tableBody, _orchestrator) {
        this.alert = _alertContainer;
        this.view = _searchContainer;
        this.noItemFound = _noItemFound;
        this.tableBody = _tableBody;
        this.table = _table;
        this.orchestrator = _orchestrator;


        this.show = function (_form) {
            homePageContainer.reset();


            var form = _form;
            var self = this;

            console.log("dentro a search element !!!!!");

            if (form.checkValidity()) {
                makeCall("POST", 'searchItems', form,
                    function (x) {
                        if (x.readyState == XMLHttpRequest.DONE) {
                            var message = x.responseText;
                            switch (x.status) {
                                case 200:
                                    if (message === "" || message.length === 0 || JSON.parse(message) === "not found") {
                                        self.noItemFound.textContent = "no item found";
                                        self.noItemFound.style.display = 'block';
                                    } else self.update(message);
                                    break;
                                default :
                                    self.alert.style.display = 'block';
                                    self.alert.innerHTML = message;
                                    break;
                            }
                        }
                    }
                );
            } else {
                form.reportValidity();
            }

        };

        this.update = function (items) {
            var self = this;

            var row, idCell, nameCell, descCell, priceCell, linkcell, anchor;

            this.tableBody.innerHTML = "";
            JSON.parse(items).forEach(function (searchMessage) {

                row = document.createElement("tr");
                idCell = document.createElement("td");
                idCell.textContent = searchMessage.item.id_item;
                row.appendChild(idCell);

                nameCell = document.createElement("td");
                nameCell.textContent = searchMessage.item.name;
                row.appendChild(nameCell);

                descCell = document.createElement("td");
                descCell.textContent = searchMessage.item.name;
                row.appendChild(descCell);

                priceCell = document.createElement("td");
                priceCell.textContent = searchMessage.price;
                row.appendChild(priceCell);

                linkcell = document.createElement("td");
                anchor = document.createElement("a");
                linkcell.appendChild(anchor);
                linkText = document.createTextNode("details");
                anchor.appendChild(linkText);

                anchor.setAttribute('itemId', searchMessage.item.id_item); // set a custom HTML attribute
                anchor.addEventListener("click", (e) => {
                    // dependency via module parameter
                    _orchestrator.showItemDetails(searchMessage.item.id_item) // the list must know the details container
                    console.log("appena creato listener a getItemDetails")
                }, false);
                anchor.href = "#";
                row.appendChild(linkcell);
                self.tableBody.appendChild(row);

                self.view.style.display = 'block';

            })


        };

        this.reset = function () {
            this.tableBody.innerHTML = "";
            this.view.style.display = 'none';
            this.noItemFound.display = 'none';
        }
    }


    function ItemDetailsContainer(
        _alertContainer,
        _id_itemDetailsContainer,
        _itemDetailsTable,
        _itemDetailsBody,
        _id_sellersDetailsTable,
        _id_sellersDetailsBody,
        _id_politica_di_spedizione,
        _orchestrator) {

        this.alert = _alertContainer;
        this.view = _id_itemDetailsContainer;
        this.itemsDetailTable = _itemDetailsTable;
        this.itemsDetailBody = _itemDetailsBody;
        this.sellerTable = _id_sellersDetailsTable;
        this.sellerdTableBody = _id_sellersDetailsBody;
        this.politicaDiSpedizione = _id_politica_di_spedizione;
        this.orchestrator = _orchestrator;


        // this.show =function () {};
        // this.update =function () {};
        // this.reset =function () {};

        this.show = function (itemId) {

            var self = this;

            this.view.style.display = 'block';

            //getItemDetails


            makeCall("POST", "ItemDetails?itemId=" + itemId
                + "&cartList=" + encodeURIComponent(sessionStorage.getItem("cartList")),
                null,
                function (x) {
                    if (x.readyState == XMLHttpRequest.DONE) {
                        var message = x.responseText;
                        switch (x.status) {
                            case 200:
                                if (message === "" || message.length === 0 || JSON.parse(message) === "not found") {
                                    self.noItemFound.textContent = "no item found";
                                    self.noItemFound.style.display = 'block';
                                } else self.update(JSON.parse(message));
                                break;
                            default :
                                self.alert.style.display = 'block';
                                self.alert.innerHTML = message;
                                break;
                        }
                    }
                }
            );


        };


        this.update = function (itemDetailsMessage) {
            //let msg = Array.from(message);
            let onSale = itemDetailsMessage.onsale;
            let pricesOnCart = itemDetailsMessage.pricesOnCart;
            let myMap = new Map(Object.entries(pricesOnCart));
            let self = this;
            let sellers = onSale.sellers;
            let prices = onSale.prices;
            let item = onSale.items[0]
            let ids_inVendita = onSale.ids_inVendita;

            console.log(itemDetailsMessage.pricesOnCart);

            let row, itemIdCell, nameItemCell, descrItemCell, catgItemCell, photoItemCell, sellerIDCell, nameCell,
                reviewCell, priceCell, freeCell, cartValue, qtyCell, inputCell, linkCell, anchorCell, shippinCell;


            //item table details
            {
                self.itemsDetailBody.innerHTML = "";
                row = document.createElement("tr");
                itemIdCell = document.createElement("td");
                itemIdCell.textContent = item.id_item;
                itemIdCell.id = "id_itemIdAddToCart";
                row.appendChild(itemIdCell);

                nameItemCell = document.createElement("td");
                nameItemCell.textContent = item.name;
                row.appendChild(nameItemCell);

                descrItemCell = document.createElement("td");
                descrItemCell.textContent = item.description;
                row.appendChild(descrItemCell);

                catgItemCell = document.createElement("td");
                catgItemCell.textContent = item.category;
                row.appendChild(catgItemCell);


                catgItemCell = document.createElement("td");
                let img = document.createElement("img");
                img.src = item.image;

                img.height = 100;
                img.width = 100;
                catgItemCell.appendChild(img);
                row.appendChild(catgItemCell);
                self.itemsDetailBody.appendChild(row);

                self.view.style.display = 'block';
            }

            //seller table details
            {
                self.sellerdTableBody.textContent = '';

                for (let i = 0; i < sellers.length; i++) {
                    let row2;

                    row2 = document.createElement("tr");
                    sellerIDCell = document.createElement("td");
                    sellerIDCell.textContent = sellers[i].sellerId;
                    row2.appendChild(sellerIDCell);

                    nameCell = document.createElement("td");
                    nameCell.textContent = sellers[i].name;
                    row2.appendChild(nameCell);

                    reviewCell = document.createElement("td");
                    reviewCell.textContent = sellers[i].rating;
                    row2.appendChild(reviewCell);

                    priceCell = document.createElement("td");
                    priceCell.textContent = prices[i];
                    row2.appendChild(priceCell);

                    freeCell = document.createElement("td");
                    freeCell.textContent = sellers[i].freeShipment;
                    row2.appendChild(freeCell);


                    cartValue = document.createElement("td");
                    cartValue.textContent = "___";

                    qtyCell = document.createElement("td");
                    qtyCell.textContent = "___";

                    if (myMap.size !== 0 && pricesOnCart[sellers[i].sellerId][0]) {
                        cartValue.textContent = pricesOnCart[sellers[i].sellerId][0];
                        qtyCell.textContent = pricesOnCart[sellers[i].sellerId][1];

                        var myCartList = JSON.parse(sessionStorage.getItem("cartList"));

                        qtyCell.addEventListener('mouseenter', ev => {
                            //Array.from(sessionStorage.getItem("cartList")).filter(seller => seller.id).map(objects)
                            let myMessage = "";
                            for (let z = 0; z < myCartList.length; z++) {
                                if (myCartList[z].seller.sellerId === sellers[i].sellerId) {
                                    let sellerItemList = myCartList[z].item;
                                    for (let w = 0; w < sellerItemList.length; w++) {
                                        myMessage += w + ") " + sellerItemList[w].name + "\n";
                                    }
                                    //myMessage += String.join("\n",myCartList.map(item => item.name));
                                    //console.log(sellerItemList);
                                }
                            }
                            window.alert("messaggio \n" + myMessage);
                        })


                    }

                    row2.appendChild(cartValue);
                    row2.appendChild(qtyCell);


                    let linkcell = document.createElement("td");
                    let div = document.createElement('div');
                    div.id = "foo";
                    //div += "<input type=\"number\" min=\"0\" name=\"id_qty\" id=\"qty\" required/> <button type=\"button\" id=\"id_addToCart\"  value=\"add to Cart\" >aggiungi</button>"

                    let input = document.createElement("input");
                    input.id = "inputID";
                    input.type = "number";

                    let text = document.createElement("textarea");
                    text.value = ids_inVendita[i];
                    text.id = "id_textInVendita";
                    text.hidden = true;

                    div.appendChild(text);
                    let addToCartButton = document.createElement("button");
                    addToCartButton.innerHTML = "Add to Cart"
                    addToCartButton.id = "addToCard";

                    addToCartButton.addEventListener('click', (e) => {

                        // shoppingCart.addItemToCart(item,prices[i],document.getElementById("inputID").value,sellers[i]);
                        //
                        console.log("clickeeed" + ids_inVendita[i] + "quantita " + document.getElementById("inputID").value);

                        console.log("cartlist creato " + sessionStorage.getItem("cartList"));


                        if (document.getElementById("inputID").value > 0) {
                            makeCall("POST", "addToCart?id_in_vendita="
                                + ids_inVendita[i] + "&itemId=" + document.getElementById("id_itemIdAddToCart").textContent
                                + "&qty=" + input.value
                                + "&cartList=" + encodeURIComponent((sessionStorage.getItem("cartList"))),
                                // +"&cartList=" +encodeURI(JSON.stringify(sessionStorage.getItem("cartList"))),
                                null,
                                function (x) {
                                    if (x.readyState == XMLHttpRequest.DONE) {
                                        var message = x.responseText;
                                        switch (x.status) {
                                            case 200:
                                                if (message === "" || message.length === 0) {
                                                    self.noItemFound.textContent = "no item found";
                                                    self.noItemFound.display = 'block';
                                                } else {
                                                    console.log("con il parse " + JSON.parse(message));
                                                    console.log("senza il pars" + message);
                                                    console.log("stamp id in vendita :" + document.getElementById("id_textInVendita").value);
                                                    sessionStorage.setItem("cartList", message);
                                                    _orchestrator.showCartPage();
                                                }
                                                break;
                                            default :
                                                self.alert.style.display = 'block';
                                                self.alert.innerHTML = message;
                                                break;
                                        }
                                    }

                                }
                            )
                        }


                    });

                    div.appendChild(input);
                    div.appendChild(addToCartButton);


                    linkcell.appendChild(div);
                    row2.appendChild(linkcell);


                    let lastTd = document.createElement("td");
                    //var body = document.getElementsByTagName('body')[0];
                    let tbl = document.createElement('table');
                    tbl.setAttribute('border', '1');
                    let thead = document.createElement("thead");
                    thead.innerHTML += "<tr>\n" +
                        "                                <th>numero minimo articoli </th>\n" +
                        "                                <th>numero massimo articoli</th>\n" +
                        "                                <th>prezzo spedizione fascia </th>\n" +
                        "                            </tr>"
                    tbl.appendChild(thead);

                    var tbdy = document.createElement('tbody');
                    let policy = sellers[i]["sPolicy"];
                    for (let j = 0; j < policy.length; j++) {
                        let tr = document.createElement('tr');

                        let minItem = document.createElement('td');
                        minItem.textContent = policy[j].minItems;
                        tr.appendChild(minItem);

                        let maxItems = document.createElement('td');
                        maxItems.textContent = policy[j].maxItems;
                        tr.appendChild(maxItems);

                        let price = document.createElement('td');
                        price.textContent = policy[j].price;
                        tr.appendChild(price);

                        tbdy.appendChild(tr);
                    }
                    tbl.appendChild(tbdy);
                    lastTd.appendChild(tbl);
                    row2.appendChild(lastTd)

                    self.sellerdTableBody.appendChild(row2);


                }

            }


        }


        this.reset = function () {
            this.view.style.display = 'none';
        };


    }


    function OrdersContainer(_alertContainer, _ordersPageContainer, _ordersPageBody, _ordersPageInfoBody, _ordersPageItemsBody) {
        this.alert = _alertContainer;
        this.view = _ordersPageContainer;
        this.infoBodyTable = _ordersPageInfoBody;
        this.itemsBody = _ordersPageItemsBody;


        this.show = function () {
            var self = this;

            self.view.innerHTML = "";

            makeCall("GET", "GotoOrders", null,
                function (x) {
                    if (x.readyState == XMLHttpRequest.DONE) {
                        var message = x.responseText;
                        switch (x.status) {
                            case 200:
                                if (message === "" || message.length === 0) {

                                } else {

                                    self.update(JSON.parse(message));
                                }
                                break;
                            default :
                                self.alert.style.display = 'block';
                                self.alert.innerHTML = message;
                                break;
                        }
                    }

                })
        };


        this.update = function (ordersObject) {
            let self = this;

            let msg = ordersObject;
            console.log("dentro ordersPage , preparo lista ");
            console.log(msg);
            // let sellers = message["sellers"]
            // let prices = message["prices"];
            // let item = message["items"][0];

            self.view.innerHTML = "";

            let viewHead = document.createElement("thead");
            viewHead.innerHTML = "<tr>\n" +
                "        <th>ordini</th>\n" +
                "\n" +
                "    </tr>";
            self.view.appendChild(viewHead);

            let viewBody = document.createElement("tbody");



            for (let k = 0; k < msg.length;  k++) {
                //let viewRow = document.createElement("tr");
                let orderTable = document.createElement("table");
                let orderTableBody = document.createElement("tbody");
                let orderRow = document.createElement("tr");

                let infoTable = document.createElement("table");
                infoTable.border = 2;
                let headInfoTable = document.createElement("thead");
                headInfoTable.innerHTML = "<tr>\n" +
                    "                    <th>seller Name</th>\n" +
                    "                    <th>Shipping date</th>\n" +
                    "                    <th>address</th>\n" +
                    "                    <th>order Id</th>\n" +
                    "                </tr>";
                infoTable.appendChild(headInfoTable);

                let bodyInfoTable = document.createElement("tbody");
                let innerRowInfoTable = document.createElement("tr");

                let sellerNameCell, shippingDateCell, addressCell, orderID;
                sellerNameCell = document.createElement("td");
                sellerNameCell.innerText = msg[k].sellerName;
                innerRowInfoTable.appendChild(sellerNameCell);

                shippingDateCell = document.createElement("td");
                shippingDateCell.innerText = msg[k].shippingDate;
                innerRowInfoTable.appendChild(shippingDateCell);


                addressCell = document.createElement("td");
                addressCell.innerText = msg[k].address;
                innerRowInfoTable.appendChild(addressCell);


                orderID = document.createElement("td");
                orderID.innerText = msg[k].orderId;
                innerRowInfoTable.appendChild(orderID);

                bodyInfoTable.appendChild(innerRowInfoTable);
                infoTable.appendChild(bodyInfoTable);


                let itemsInfoTable = document.createElement("table");
                itemsInfoTable.border = 1;
                let itemInfoHead = document.createElement(("thead"));
                itemInfoHead.innerHTML = "<tr>\n" +
                    "                    <th>id oggetto</th>\n" +
                    "                    <th>nome</th>\n" +
                    "                    <th>quantita</th>\n" +
                    "                </tr>";
                itemsInfoTable.appendChild(itemInfoHead);


                let bodyItemTable = document.createElement("tbody");

                let lightItem = msg[k].itemList;

                for (j = 0; j < lightItem.length; j++) {
                    let itemRow, itemIdCell, itemNAmeCell, qtyCell;

                    itemRow = document.createElement("tr");

                    itemIdCell = document.createElement("td");
                    itemIdCell.innerText = lightItem[j].itemId;
                    itemRow.appendChild(itemIdCell);

                    itemNAmeCell = document.createElement("td");
                    itemNAmeCell.innerText = lightItem[j].name;
                    itemRow.appendChild(itemNAmeCell);

                    qtyCell = document.createElement("td");
                    qtyCell.innerText = lightItem[j].qty;
                    itemRow.appendChild(qtyCell);


                    bodyItemTable.appendChild(itemRow);
                }

                itemsInfoTable.appendChild(bodyItemTable);

                orderRow.appendChild(infoTable);
                orderRow.appendChild(itemsInfoTable);
                orderTableBody.appendChild(orderRow);
                orderTable.appendChild(orderTableBody);


                viewBody.appendChild(orderTable);
                self.view.appendChild(viewBody);
            }



            self.view.style.display = 'block';


        };


        this.reset = function () {
            let self = this;

            // self.infoBodyTable.innerHTML = "";
            // self.itemsBody.innerHTML = "";
            self.view.innerHTML = "";
            self.view.style.display = 'none';
        };


    }


    function CartPageContainer(_alertContainer, _cartPageContainer, _cartPageBody, _orchestrator) {
        this.alert = _alertContainer;
        this.view = _cartPageContainer;
        this.tableBody = _cartPageBody;
        this.orchestrator = _orchestrator;


        // this.show =function () {};
        // this.update =function () {};
        // this.reset =function () {};

        this.show = function () {
            var self = this;

            self.tableBody.innerHTML = "";
            let cartList = sessionStorage.getItem("cartList");
            let obj = JSON.parse(cartList)

            self.update();

        }

        this.update = function () {
            var self = this;

            let cartList = sessionStorage.getItem("cartList");
            console.log(cartList);
            let obj = JSON.parse(cartList)
            console.log(obj);


            self.tableBody.innerHTML = "";
            self.tableBody.width = "100%";


            for (let i = 0; i < obj.length; i++) {
                let externalRow = document.createElement("tr");

                let nameCell, priceCell, shippingCost, itemTable;

                nameCell = document.createElement("td");
                nameCell.innerHTML = obj[i].seller.sellerId;
                nameCell.id = "id_sellerNewOrder";
                nameCell.value = obj[i].seller.sellerId;
                externalRow.appendChild(nameCell);

                priceCell = document.createElement("td");
                priceCell.innerHTML = obj[i].totalPrice;
                externalRow.appendChild(priceCell);

                shippingCost = document.createElement("td");
                shippingCost.innerHTML = obj[i].shippingPrice === 0 ? "gratis" : obj[i].shippingPrice;
                externalRow.appendChild(shippingCost);

                let lastTd = document.createElement("td");
                //var body = document.getElementsByTagName('body')[0];
                //tbl.setAttribute('border', '1');
                // let thead = document.createElement("thead");
                // thead.innerHTML += "<tr>\n" +
                //     "                                <th>articoli</th>\n" +
                //     "                            </tr>";


                //let tbdy = document.createElement("tbody");

                //let itemList = obj[i];


                var tbl = document.createElement('table');
                tbl.style.width = '100%';
                tbl.setAttribute('border', '1');
                var tbdy = document.createElement('tbody');

                let currentItem = obj[i].item;
                for (let j = 0; j < currentItem.length; j++) {

                    let innerRow = document.createElement('tr');
                    let td = document.createElement('td');
                    td.innerText = currentItem[j].name;
                    innerRow.appendChild(td);
                    tbdy.appendChild(innerRow);
                }


                tbl.appendChild(tbdy);
                externalRow.appendChild(tbl)
                let buyTd = document.createElement("td");
                let buyButton = document.createElement("button");
                buyButton.addEventListener('click', ev => {
                        if (document.getElementById("id_sellerNewOrder").value > 0) {
                            makeCall("POST", "insertNewOrder?" +
                                "sellerdID=" + document.getElementById("id_sellerNewOrder").value
                                + "&cartList=" + encodeURIComponent((sessionStorage.getItem("cartList"))),
                                // +"&cartList=" +encodeURI(JSON.stringify(sessionStorage.getItem("cartList"))),
                                null,
                                function (x) {
                                    if (x.readyState == XMLHttpRequest.DONE) {
                                        var message = x.responseText;
                                        switch (x.status) {
                                            case 200:
                                                if (message === "" || message.length === 0) {
                                                    self.noItemFound.textContent = "no item found";
                                                    self.noItemFound.style.display = 'block';
                                                } else {

                                                    self.orchestrator.goToOrdersPage();
                                                }
                                                break;
                                            default :
                                                self.alert.style.display = 'block';
                                                self.alert.innerHTML = message;
                                                break;
                                        }
                                    }

                                }
                            )
                        }
                    }
                )
                buyButton.value = "buy";
                buyButton.textContent = "    buy!    ";

                buyTd.appendChild(buyButton);
                externalRow.appendChild(buyTd);


                self.tableBody.appendChild(externalRow);


            }
            self.view.style.display = 'block'


        }


        this.reset = function () {
            this.view.style.display = 'none';
            this.tableBody.innerHTML = "";
        };


    }


    function PageOrchestrator() {
        var alertContainer = document.getElementById("id_alert");

        this.start = function () {


            personalMessage = new PersonalMessage(
                sessionStorage.getItem("username"),
                document.getElementById("id_username")
            );

            document.getElementById("id_GoToHomePage").addEventListener('click', (ev => {
                this.refresh();
                homePageContainer.show();

            }));

            document.getElementById("id_GoToShoppingCart").addEventListener('click', (ev => {
                this.refresh();
                cartPageContainer.show();

            }));

            document.getElementById("id_GoToOrders").addEventListener('click', (ev => {
                this.refresh();
                ordersContainer.show();

            }));


            ordersContainer = new OrdersContainer(
                alertContainer,
                document.getElementById("id_ordersPageContainer"),
                document.getElementById("ordersPageBody"),
                document.getElementById("ordersPageInfoBody"),
                document.getElementById("ordersPageItemsBody")
            )

            cartPageContainer = new CartPageContainer(
                alertContainer,
                document.getElementById("cartPageContainer"),
                document.getElementById("cartPageBody"),
                this
            )

            homePageContainer = new HomePageContainer(
                alertContainer,
                document.getElementById("homeViewContainer"),
                document.getElementById("id_lastSeenItemsTableContainer"),
                document.getElementById("id_searchItem"),
                this,
                document.getElementById("id_lastSeenItemsTableBody"),
            );

            searchElementContainer = new SearchElementContainer(
                alertContainer,
                document.getElementById("id_searchElementContainer"),
                document.getElementById("id_searchTable"),
                document.getElementById("id_noItemFound"),
                document.getElementById("id_searchTableBody"),
                this
            );

            itemDetailsContainer = new ItemDetailsContainer(
                alertContainer,
                document.getElementById("id_itemDetailsContainer"),
                document.getElementById("itemDetailsTable"),
                document.getElementById("itemDetailsBody"),
                document.getElementById("id_sellersDetailsTable"),
                document.getElementById("id_sellersDetailsBody"),
                document.getElementById("id_politica di spedizione"),
                this
            )


            document.getElementById("Logout").addEventListener('click', ev => {
                window.sessionStorage.removeItem('username');
                window.red = "../index";
            })


        }

        this.refresh = function () {
            alertContainer.textContent = '';
            alertContainer.style.display = 'none';
            searchElementContainer.reset();
            itemDetailsContainer.reset();
            homePageContainer.reset();
            cartPageContainer.reset();
            ordersContainer.reset();

        }

        this.showSearchElement = function (result) {
            this.refresh();

            searchElementContainer.show(result);
        }

        this.showItemDetails = function (itemId) {
            this.refresh();
            itemDetailsContainer.show(itemId);
        }

        this.showCartPage = function () {

            this.refresh();
            cartPageContainer.show();
        }

        this.goToOrdersPage = function () {
            this.refresh();
            ordersContainer.show();
        }

        this.goToHomePage = function () {
            this.refresh();
            homePageContainer.show();
        }


    }
}