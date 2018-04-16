package com.sbt.bitcoin.wallet.control;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.sbt.component.table.SbtTableRowData;
import com.sbt.component.table.SbtTableView2;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.bitcoinj.wallet.DeterministicKeyChain;

import java.net.URL;
import java.util.ResourceBundle;

import static com.sbt.bitcoin.wallet.control.GuiUtils.checkGuiThread;
import static com.sbt.bitcoin.wallet.control.WalletMain.bitcoin;

public class ShowKeyTableController implements Initializable{

    public WalletMain.OverlayUI overlayUI;
    @FXML private SbtTableView2<PPKeysAddress> keysTable;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        show();
    }
    public void close() {
        checkGuiThread();
        overlayUI.done();
    }
    private void show() {
        DeterministicKeyChain keyChain = bitcoin.wallet().getActiveKeyChain();
        System.out.println("issuedExternalKeys:"+keyChain.getIssuedExternalKeys()+", IssuedInternal:"+keyChain.getIssuedInternalKeys()+", receivekey="+keyChain.getIssuedReceiveKeys());
        for (int i=0;i<100;i++) {
            PPKeysAddress rowData = new PPKeysAddress();
            rowData.setAddress("address"+i);
            rowData.setPrivateKey("privateKey"+i);
            rowData.setPublicKey("publicKey"+i);
            keysTable.getItems().add(rowData);
        }
    }
/////////////////////////////////////////////////////////////////////////////////
    public static class PPKeysAddress extends SbtTableRowData {
        private String privateKey;
        private String publicKey;
        private String address;

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
