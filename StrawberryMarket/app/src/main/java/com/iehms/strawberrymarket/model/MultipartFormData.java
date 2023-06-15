package com.iehms.strawberrymarket.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MultipartFormData {

    private static class DataItem {
        String name;
        byte[] data;     // 1(String) : 미리 변환된 데이터, 2(byte)일경우 입력데이터의 복사본, 3(memory file)일경우 file data
        String fileName; // 전송될 파일이름
        String filePath; // local상의 파일이름 // 4
    }

    private List<DataItem> mList = new ArrayList< DataItem >();

    // 1. java를 쓸 것이기 때문에 String 데이터 전송
    public void addString( String name, String value, String charset ) throws UnsupportedEncodingException {

        DataItem d = new DataItem();

        d.name = name;
        d.data = value.getBytes( charset );

        mList.add( d );
    }
    // 2. binary데이터 전송
    public void addBinary( String name, byte[] value ) {

        DataItem d = new DataItem();

        d.name = name;
        if( value != null ) {
            d.data = new byte[value.length];
            System.arraycopy(value, 0, d.data, 0, value.length);
        }

        mList.add( d );
    }
    // 3. 파일로 binary전송 : 실제 데이터가 메모리상에 있고 이 것을 서버에서 파일로 인식하도록 전송
    public void addFile( String name, String filename, byte[] value ) {

        DataItem d = new DataItem();

        d.name = name;
        d.fileName = filename;
        if( value != null ) {
            d.data = new byte[value.length];
            System.arraycopy(value, 0, d.data, 0, value.length);
        }

        mList.add(d);
    }
    // 4. local 파일로 데이터전송 : 데이터가 파일로 있는 경우
    public void addFile( String name, String filename ) {

        DataItem d = new DataItem();
        d.name = name;
        d.fileName = new File( filename ).getName();
        d.filePath = filename;
        mList.add(d);
    }

    public int computeContentLength(String boundary) {
        int ret = 0;

        int boundarylen = boundary.length();

        if( mList.size() == 0 ) {
            return 0;
        }

        for( DataItem d : mList ) {
            //--{boundary}RN
            //12{boundary}12
            ret += 2 + boundarylen + 2;

            // Content-Disposition: form-data; name="{name}"
            //          1         2         3
            // 12345678901234567890123456789012345678{name}1
            //
            ret += 38 + d.name.length() + 1;

            if( d.fileName == null ) {
                ret += 4; // \r\n\r\n
                ret += d.data.length;
            } else {

                // ; filename="{filename}"
                // 123456789012{filename}1
                ret += 12 + d.fileName.length() + 1;
                ret += 4; // \r\n\r\n
                if( d.data != null ) {
                    ret += d.data.length;
                } else {
                    // case 4
                    ret += new File(d.filePath).length();
                }
            }
            ret += 2;  // "\r\n"
        }

        // --boundary--RN
        // 12        1234
        ret += 2 + boundarylen + 4;

        return ret;
    }

    public int write( String boundary, OutputStream out ) throws IOException {

        int ret = 0;

        byte[] buf = new byte[1024];

        int boundarylen = boundary.length();
        byte[] dd    = { '-', '-' };
        byte[] rn    = { '\r', '\n' };
        byte[] cdf   = ("Content-Disposition: form-data; name=\"").getBytes();
        byte[] fn    = "\"; filename=\"".getBytes();
        byte[] q     = {'"'};

        byte[] bound = boundary.getBytes( "UTF-8" );

        if( mList.size() == 0 ) {
            return 0;
        }

        for( DataItem d : mList ) {


            ret += 2 + boundarylen + 2;
            out.write( dd ); out.write( bound ); out.write( rn );

            if( d.fileName == null ) {

                out.write( cdf ); out.write( d.name.getBytes(StandardCharsets.UTF_8) ); out.write( '"' ); out.write( rn );
                ret += 38 + d.name.length() + 1 + 2;

                out.write( rn );
                ret += 2; // \r\n

                out.write( d.data );
                ret += d.data.length;

            } else {

                out.write( cdf );
                out.write( d.name.getBytes(StandardCharsets.UTF_8) );
                out.write( fn );
                out.write( d.fileName.getBytes(StandardCharsets.UTF_8) );
                out.write( '"' );
                out.write( rn );

                ret += 38 + d.name.length() + 13 + d.fileName.length() + 1 + 2;

                out.write( rn );
                ret += 2; // \r\n


                if( d.data != null ) {
                    out.write( d.data );
                    ret += d.data.length;

                } else {
                    // case 4

                    File f = new File(d.filePath);

                    FileInputStream fo = new FileInputStream( f );

                    BufferedInputStream is = new BufferedInputStream( fo );

                    while( true ) {
                        int nread = is.read( buf );
                        if( nread <= 0  ) {
                            break;
                        }
                        out.write( buf, 0, nread );
                    }


                    fo.close();

                    ret += f.length();
                }
            }

            out.write( rn );
            ret += 2;  // "\r\n"
        }

        // --boundary--RN
        // 12        1234
        out.write( dd );
        out.write( bound );
        out.write( dd );
        out.write( rn );
        ret += 2 + boundarylen + 4;

        return ret;
    }
}