package com.example.nfcdeneme3;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentFilter;
import android.nfc.*;
import android.nfc.tech.Ndef;
import android.os.Parcelable;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nfcdeneme3.databinding.NfcWriterMainBinding;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;

public class NFCWriter extends AppCompatActivity  {
    private NfcWriterMainBinding binding;
    public IntentFilter[] writeTagFilters;
    private TextView tvNFCContent;
    private TextView message;
    private Button btnWrite;
    @Nullable
    private NfcAdapter nfcAdapter;
    @Nullable
    private PendingIntent pendingIntent;
    private boolean writeMode;
    @Nullable
    private Tag myTag;
    @NotNull
    public static final String ERROR_DETECTED = "No NFC tag detected!";
    @NotNull
    public static final String WRITE_SUCCESS = "Text written to the NFC tag successfully!";
    @NotNull
    public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";
    @NotNull
    public static final NFCWriter.Companion Companion = new NFCWriter.Companion((DefaultConstructorMarker)null);

    @NotNull
    public final IntentFilter[] getWriteTagFilters() {
        IntentFilter[] var10000 = this.writeTagFilters;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("writeTagFilters");
        }

        return var10000;
    }

    public final void setWriteTagFilters(@NotNull IntentFilter[] var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.writeTagFilters = var1;
    }

    @Nullable
    public final NfcAdapter getNfcAdapter() {
        return this.nfcAdapter;
    }

    public final void setNfcAdapter(@Nullable NfcAdapter var1) {
        this.nfcAdapter = var1;
    }

    @Nullable
    public final PendingIntent getPendingIntent() {
        return this.pendingIntent;
    }

    public final void setPendingIntent(@Nullable PendingIntent var1) {
        this.pendingIntent = var1;
    }

    public final boolean getWriteMode() {
        return this.writeMode;
    }

    public final void setWriteMode(boolean var1) {
        this.writeMode = var1;
    }

    @Nullable
    public final Tag getMyTag() {
        return this.myTag;
    }

    public final void setMyTag(@Nullable Tag var1) {
        this.myTag = var1;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_writer_main);

        Button next = (Button) findViewById(R.id.button);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        });

        NfcWriterMainBinding var10001 = NfcWriterMainBinding.inflate(this.getLayoutInflater());
        Intrinsics.checkNotNullExpressionValue(var10001, "NfcWriterMainBinding.inflate(layoutInflater)");
        this.binding = var10001;
        var10001 = this.binding;
        if (var10001 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }

        this.setContentView((View) var10001.getRoot());
        var10001 = this.binding;
        if (var10001 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }

        TextView var3 = var10001.nfcContents;
        Intrinsics.checkNotNullExpressionValue(var3, "binding.nfcContents");
        this.tvNFCContent = var3;
        var10001 = this.binding;
        if (var10001 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }

        EditText var4 = var10001.editMessage;
        Intrinsics.checkNotNullExpressionValue(var4, "binding.editMessage");
        this.message = (TextView) var4;
        var10001 = this.binding;
        if (var10001 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }

        Button var5 = var10001.button;
        Intrinsics.checkNotNullExpressionValue(var5, "binding.button");
        this.btnWrite = var5;
        Button var10000 = this.btnWrite;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("btnWrite");
        }

        var10000.setOnClickListener((View.OnClickListener) (new View.OnClickListener() {
            public final void onClick(View it) {
                try {
                    if (NFCWriter.this.getMyTag() == null) {
                        Toast.makeText((Context) NFCWriter.this, (CharSequence) "No NFC tag detected!", Toast.LENGTH_LONG).show();
                    } else {
                        NFCWriter.this.write(NFCWriter.access$getMessage$p(NFCWriter.this).getText().toString(), NFCWriter.this.getMyTag());
                        Toast.makeText((Context) NFCWriter.this, (CharSequence) "Text written to the NFC tag successfully!", Toast.LENGTH_LONG).show();
                    }
                } catch (IOException var3) {
                    Toast.makeText((Context) NFCWriter.this, (CharSequence) "Error during writing, is the NFC tag close enough to your device?", Toast.LENGTH_LONG).show();
                    var3.printStackTrace();
                } catch (FormatException var4) {
                    Toast.makeText((Context) NFCWriter.this, (CharSequence) "Error during writing, is the NFC tag close enough to your device?", Toast.LENGTH_LONG).show();
                    var4.printStackTrace();
                }

            }
        }));
        this.nfcAdapter = NfcAdapter.getDefaultAdapter((Context) this);
        if (this.nfcAdapter == null) {
            Toast.makeText((Context) this, (CharSequence) "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            this.finish();
        }

        Intent var6 = this.getIntent();
        Intrinsics.checkNotNullExpressionValue(var6, "intent");
        this.readFromIntent(var6);
        this.pendingIntent = PendingIntent.getActivity((Context) this, 0, (new Intent((Context) this, this.getClass())).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter("android.nfc.action.TAG_DISCOVERED");
        tagDetected.addCategory("android.intent.category.DEFAULT");
        this.writeTagFilters = new IntentFilter[]{tagDetected};
    }

    private final void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (Intrinsics.areEqual("android.nfc.action.TAG_DISCOVERED", action) || Intrinsics.areEqual("android.nfc.action.TECH_DISCOVERED", action) || Intrinsics.areEqual("android.nfc.action.NDEF_DISCOVERED", action)) {
            this.myTag = (Tag)intent.getParcelableExtra("android.nfc.extra.TAG");
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra("android.nfc.extra.NDEF_MESSAGES");
            List msgs = (List)(new ArrayList());
            if (rawMsgs != null) {
                int i = 0;

                for(int var6 = rawMsgs.length; i < var6; ++i) {
                    Parcelable var10002 = rawMsgs[i];
                    if (rawMsgs[i] == null) {
                        throw new NullPointerException("null cannot be cast to non-null type android.nfc.NdefMessage");
                    }

                    msgs.add(i, (NdefMessage)var10002);
                }

                Collection $this$toTypedArray$iv = (Collection)msgs;
                boolean $i$f$toTypedArray = false;
                Object[] var10001 = $this$toTypedArray$iv.toArray(new NdefMessage[0]);
                if (var10001 == null) {
                    throw new NullPointerException("null cannot be cast to non-null type kotlin.Array<T of kotlin.collections.ArraysKt__ArraysJVMKt.toTypedArray>");
                }

                this.buildTagViews((NdefMessage[])var10001);
            }
        }

    }

    private final void buildTagViews(NdefMessage[] msgs) {
        if (msgs != null && msgs.length != 0) {
            String text = "";
            NdefRecord var10000 = msgs[0].getRecords()[0];
            Intrinsics.checkNotNullExpressionValue(var10000, "msgs[0].records[0]");
            byte[] payload = var10000.getPayload();
            byte languageCodeLength = payload[0];
            byte var6 = (byte)128;
            Charset textEncoding = (byte)(languageCodeLength & var6) == 0 ? Charsets.UTF_8 : Charsets.UTF_16;
            var6 = payload[0];
            byte var7 = 51;
            languageCodeLength = (byte)(var6 & var7);

            try {
                Intrinsics.checkNotNullExpressionValue(payload, "payload");
                int var10 = languageCodeLength + 1;
                int var8 = payload.length - languageCodeLength - 1;
                text = new String(payload, var10, var8, textEncoding);
            } catch (Exception var9) {
                Log.e("UnsupportedEncoding", var9.toString());
            }

            TextView var11 = this.tvNFCContent;
            if (var11 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("tvNFCContent");
            }

            var11.setText((CharSequence)("Message read from NFC Tag:\n " + text));
        }
    }

    private final void write(String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = new NdefRecord[]{this.createRecord(text)};
        NdefMessage message = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }

    private final NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang = "en";
        Charset var5 = Charsets.UTF_8;
        byte[] var10000 = text.getBytes(var5);
        Intrinsics.checkNotNullExpressionValue(var10000, "this as java.lang.String).getBytes(charset)");
        byte[] textBytes = var10000;
        String var6 = "US-ASCII";
        Charset var9 = Charset.forName(var6);
        Intrinsics.checkNotNullExpressionValue(var9, "forName(charsetName)");
        Charset var10 = var9;
        var10000 = lang.getBytes(var10);
        Intrinsics.checkNotNullExpressionValue(var10000, "this as java.lang.String).getBytes(charset)");
        byte[] langBytes = var10000;
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[1 + langLength + textLength];
        payload[0] = (byte)langLength;
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);
        return new NdefRecord((short)1, NdefRecord.RTD_TEXT, new byte[0], payload);
    }

    protected void onNewIntent(@NotNull Intent intent) {
        super.onNewIntent(intent);
        Intrinsics.checkNotNullParameter(intent, "intent");
        this.setIntent(intent);
        this.readFromIntent(intent);
        if (Intrinsics.areEqual("android.nfc.action.TAG_DISCOVERED", intent.getAction())) {
            this.myTag = (Tag) intent.getParcelableExtra("android.nfc.extra.TAG");
        }

    }

    public void onPause() {
        super.onPause();
        this.WriteModeOff();
    }

    public void onResume() {
        super.onResume();
        this.WriteModeOn();
    }

    private final void WriteModeOn() {
        this.writeMode = true;
        NfcAdapter var10000 = this.nfcAdapter;
        Intrinsics.checkNotNull(var10000);
        Activity var10001 = (Activity)this;
        PendingIntent var10002 = this.pendingIntent;
        IntentFilter[] var10003 = this.writeTagFilters;
        if (var10003 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("writeTagFilters");
        }

        var10000.enableForegroundDispatch(var10001, var10002, var10003, (String[][])null);
    }

    private final void WriteModeOff() {
        this.writeMode = false;
        NfcAdapter var10000 = this.nfcAdapter;
        Intrinsics.checkNotNull(var10000);
        var10000.disableForegroundDispatch((Activity)this);
    }

    // $FF: synthetic method
    public static final TextView access$getMessage$p(NFCWriter $this) {
        TextView var10000 = $this.message;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("message");
        }

        return var10000;
    }

    // $FF: synthetic method
    public static final void access$setMessage$p(NFCWriter $this, TextView var1) {
        $this.message = var1;
    }

    @Metadata(
            mv = {1, 6, 0},
            k = 1,
            d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0007"},
            d2 = {"Lcom/example/nfcdeneme3/NFCWriter$Companion;", "", "()V", "ERROR_DETECTED", "", "WRITE_ERROR", "WRITE_SUCCESS", "NFC_Deneme_3.app.main"}
    )
    public static final class Companion {
        private Companion() {
        }

        // $FF: synthetic method
        public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

}
