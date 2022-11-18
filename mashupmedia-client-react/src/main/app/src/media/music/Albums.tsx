import { Grid } from "@mui/material"
import { is } from "immer/dist/internal"
import { useCallback, useEffect, useRef, useState } from "react"
import { useSelector } from "react-redux"
import AlbumSummary from "../../common/components/media/AlbumSummary"
import { RootState } from "../../common/redux/store"
import { SecureMediaPayload } from "../rest/secureMediaPayload"
import { AlbumWithArtistPayload, getRandomAlbums } from "./rest/musicCalls"


type InfiniteScrollPayload = {
    page: number
    loading: boolean
}

const Albums = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const [infiniteScrollProps, setInfiniteScrollProps] = useState<InfiniteScrollPayload>({
        page: 0,
        loading: false
    })

    const [props, setProps] = useState<SecureMediaPayload<AlbumWithArtistPayload>[]>()


    const setInfiniteScrollLoadingProps = (loading: boolean) => {
        setInfiniteScrollProps(p => ({
            ...p,
            loading: loading
        }))
    }

    const setInfiniteScrollPageProps = (page: number) => {
        setInfiniteScrollProps(p => ({
            ...p,
            page: page
        }))
    }

    const loadContent = (userToken?: string) => {
        if (infiniteScrollProps.loading) {
            return
        }

        setInfiniteScrollLoadingProps(true)
        getRandomAlbums(userToken)
            .then(response => {
                if (response.parsedBody) {
                    if (props) {
                        setProps(props.concat(response.parsedBody))
                    } else {
                        setProps(response.parsedBody)
                    }
                }
            })
            .finally(() => setInfiniteScrollLoadingProps(false))
    }

    useEffect(() => {
        loadContent(userToken)
    }, [userToken, infiniteScrollProps.page])

    const observer = useRef<IntersectionObserver>();
    const lastItemRef = useCallback(
        (node) => {
            if (infiniteScrollProps.loading) {
                return
            }

            if (observer.current) {
                observer.current.disconnect();
            }

            observer.current = new IntersectionObserver((entries) => {
                if (entries[0].isIntersecting) {
                    setInfiniteScrollPageProps(infiniteScrollProps.page + 1)
                }
            });

            if (node) observer.current.observe(node);
        },
        [infiniteScrollProps.loading]
    );


    return (
        <div>

            <Grid container spacing={5} columns={{ xs: 4, sm: 8, md: 12 }} display="flex">

                {props?.map(function (albumWithArtistPayload, index) {

                    let isLast = false
                    if (props.length === index + 1) {
                        isLast = true
                    }

                    return (
                        <Grid
                            item
                            key={index}
                            xs={4}
                            sm={4}
                            md={4}
                            ref={isLast ? lastItemRef : null}
                        >
                            <AlbumSummary
                                mediaToken={albumWithArtistPayload.mediaToken}
                                payload={albumWithArtistPayload.payload}
                            />
                        </Grid>
                    )
                })}
            </Grid>
        </div>
    )
}

export default Albums